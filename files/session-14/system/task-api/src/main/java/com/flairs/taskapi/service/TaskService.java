package com.flairs.taskapi.service;

import com.flairs.taskapi.config.CacheConfig;
import com.flairs.taskapi.dto.CreateTaskRequest;
import com.flairs.taskapi.dto.PageResponse;
import com.flairs.taskapi.dto.PriorityCount;
import com.flairs.taskapi.dto.TaskMapper;
import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.dto.UpdateTaskRequest;
import com.flairs.taskapi.exception.DuplicateException;
import com.flairs.taskapi.events.TaskEvent;
import com.flairs.taskapi.exception.NotFoundException;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Project;
import com.flairs.taskapi.model.Tag;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.repository.TagRepository;
import com.flairs.taskapi.repository.TaskRepository;
import com.flairs.taskapi.security.AuthUser;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Session 12: a task belongs to a project, and the project has an owner.
// So "can this caller touch this task?" = "does the caller own the task's project?" (or is ADMIN).
// Session 14: stats are cached in Redis, and creating or completing a task publishes a TaskEvent.
@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository tasks;
    private final ProjectService projects;
    private final TagRepository tags;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public TaskService(TaskRepository tasks, ProjectService projects, TagRepository tags,
                       ApplicationEventPublisher events, Clock clock) {
        this.tasks = tasks;
        this.projects = projects;
        this.tags = tags;
        this.events = events;
        this.clock = clock;
    }

    public PageResponse<TaskResponse> search(AuthUser caller, Boolean done, Priority priority, Pageable pageable) {
        return PageResponse.of(tasks.search(caller.ownerFilter(), done, priority, pageable), TaskMapper::toResponse);
    }

    public TaskResponse findById(Long id, AuthUser caller) {
        Task task = tasks.findWithDetailsById(id).orElseThrow(() -> new NotFoundException("Task", id));
        checkVisible(task, caller);
        return TaskMapper.toResponse(task);
    }

    public List<TaskResponse> overdue(AuthUser caller) {
        return tasks.findOverdue(LocalDate.now(), caller.ownerFilter()).stream().map(TaskMapper::toResponse).toList();
    }

    // The first call per user runs the GROUP BY query and stores the answer in Redis (key "task-stats::2").
    // Later calls return the stored answer without touching PostgreSQL, until a change evicts it or the TTL ends.
    // The admin sees everyone's tasks (ownerFilter() is null), so the admin's entry is stored under "all".
    @Cacheable(cacheNames = CacheConfig.TASK_STATS, key = "#caller.ownerFilter() ?: 'all'")
    public List<PriorityCount> stats(AuthUser caller) {
        return tasks.countByPriority(caller.ownerFilter());
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.TASK_STATS, allEntries = true)   // counts changed: forget all stats
    public TaskResponse create(CreateTaskRequest request, AuthUser caller) {
        // You can only add tasks to a project you can see: 404 otherwise
        Project project = projects.getVisible(request.projectId(), caller);
        String title = request.title().strip();
        if (tasks.existsByProjectIdAndTitleIgnoreCase(project.getId(), title)) {
            throw new DuplicateException("A task titled '" + title + "' already exists in this project");
        }
        Task task = new Task(title, request.description(), request.priority(), request.dueDate(), request.estimateHours(), project);
        task.getTags().addAll(findOrCreateTags(request.tags()));
        Task saved = tasks.save(task);
        log.info("User {} created task {} in project {}", caller.id(), saved.getId(), project.getId());
        publish(TaskEvent.Type.CREATED, saved);
        return TaskMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.TASK_STATS, allEntries = true)   // counts changed: forget all stats
    public TaskResponse update(Long id, UpdateTaskRequest request, AuthUser caller) {
        Task task = getVisible(id, caller);
        task.setTitle(request.title().strip());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDone(request.done());
        task.setDueDate(request.dueDate());
        task.setEstimateHours(request.estimateHours());
        tasks.flush();
        return TaskMapper.toResponse(task);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.TASK_STATS, allEntries = true)   // counts changed: forget all stats
    public TaskResponse complete(Long id, AuthUser caller) {
        Task task = getVisible(id, caller);
        if (!task.isDone()) {                       // completing twice sends one event, not two
            task.complete();
            publish(TaskEvent.Type.COMPLETED, task);
        }
        return TaskMapper.toResponse(task);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.TASK_STATS, allEntries = true)   // counts changed: forget all stats
    public void delete(Long id, AuthUser caller) {
        tasks.delete(getVisible(id, caller));
    }

    // A Spring event, not a RabbitMQ call: TaskEventPublisher sends it to RabbitMQ after the commit
    private void publish(TaskEvent.Type type, Task task) {
        events.publishEvent(new TaskEvent(type, task.getId(), task.getTitle(),
                task.getProject().getOwner().getId(), Instant.now(clock)));
    }

    private Task getVisible(Long id, AuthUser caller) {
        Task task = tasks.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
        checkVisible(task, caller);
        return task;
    }

    private void checkVisible(Task task, AuthUser caller) {
        if (!caller.isAdmin() && !task.getProject().isOwnedBy(caller.id())) {
            throw new NotFoundException("Task", task.getId());
        }
    }

    private List<Tag> findOrCreateTags(Set<String> names) {
        if (names == null) {
            return List.of();
        }
        return names.stream()
                .map(name -> name.strip().toLowerCase())
                .distinct()
                .map(name -> tags.findByName(name).orElseGet(() -> tags.save(new Tag(name))))
                .toList();
    }
}
