package com.flairs.taskapi.service;

import com.flairs.taskapi.dto.CreateTaskRequest;
import com.flairs.taskapi.dto.PageResponse;
import com.flairs.taskapi.dto.PriorityCount;
import com.flairs.taskapi.dto.TaskMapper;
import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.dto.UpdateTaskRequest;
import com.flairs.taskapi.exception.DuplicateException;
import com.flairs.taskapi.exception.NotFoundException;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Project;
import com.flairs.taskapi.model.Tag;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.repository.ProjectRepository;
import com.flairs.taskapi.repository.TagRepository;
import com.flairs.taskapi.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// The service is the transaction boundary: one public method = one transaction = all or nothing.
// It returns DTOs, so lazy relations are read while the transaction (and the database session) is still open.
@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository tasks;
    private final ProjectRepository projects;
    private final TagRepository tags;

    public TaskService(TaskRepository tasks, ProjectRepository projects, TagRepository tags) {
        this.tasks = tasks;
        this.projects = projects;
        this.tags = tags;
    }

    public PageResponse<TaskResponse> search(Boolean done, Priority priority, Pageable pageable) {
        return PageResponse.of(tasks.search(done, priority, pageable), TaskMapper::toResponse);
    }

    public TaskResponse findById(Long id) {
        return TaskMapper.toResponse(tasks.findWithDetailsById(id).orElseThrow(() -> new NotFoundException("Task", id)));
    }

    public List<TaskResponse> overdue() {
        return tasks.findByDueDateBeforeAndDoneFalseOrderByDueDate(LocalDate.now()).stream().map(TaskMapper::toResponse).toList();
    }

    public List<PriorityCount> stats() {
        return tasks.countByPriority();
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        String title = request.title().strip();
        if (tasks.existsByTitleIgnoreCase(title)) {
            throw new DuplicateException("A task titled '" + title + "' already exists");
        }
        Project project = projects.findById(request.projectId())
                .orElseThrow(() -> new NotFoundException("Project", request.projectId()));
        Task task = new Task(title, request.description(), request.priority(), request.dueDate(), request.estimateHours(), project);
        task.getTags().addAll(findOrCreateTags(request.tags()));
        Task saved = tasks.save(task);
        log.info("Created task {} in project {}", saved.getId(), project.getId());
        return TaskMapper.toResponse(saved);
    }

    @Transactional
    public TaskResponse update(Long id, UpdateTaskRequest request) {
        Task task = get(id);
        task.setTitle(request.title().strip());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDone(request.done());
        task.setDueDate(request.dueDate());
        task.setEstimateHours(request.estimateHours());
        // No save() call: the entity is "managed", so Hibernate sees the changes and runs the UPDATE at commit.
        // flush() runs that UPDATE now, so the response already shows the new updatedAt.
        tasks.flush();
        return TaskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse complete(Long id) {
        Task task = get(id);
        task.complete();
        return TaskMapper.toResponse(task);
    }

    @Transactional
    public void delete(Long id) {
        tasks.delete(get(id));
    }

    private Task get(Long id) {
        return tasks.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
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
