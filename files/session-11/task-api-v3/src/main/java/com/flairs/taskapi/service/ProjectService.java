package com.flairs.taskapi.service;

import com.flairs.taskapi.dto.CreateProjectRequest;
import com.flairs.taskapi.dto.ProjectResponse;
import com.flairs.taskapi.dto.TaskMapper;
import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.exception.DuplicateException;
import com.flairs.taskapi.exception.NotFoundException;
import com.flairs.taskapi.model.Project;
import com.flairs.taskapi.repository.ProjectRepository;
import com.flairs.taskapi.repository.TaskRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)      // every method runs in a read-only transaction unless it says otherwise
public class ProjectService {

    private final ProjectRepository projects;
    private final TaskRepository tasks;

    public ProjectService(ProjectRepository projects, TaskRepository tasks) {
        this.projects = projects;
        this.tasks = tasks;
    }

    public List<ProjectResponse> findAll() {
        return projects.findAll(Sort.by("name")).stream().map(TaskMapper::toResponse).toList();
    }

    public ProjectResponse findById(Long id) {
        return TaskMapper.toResponse(get(id));
    }

    public List<TaskResponse> tasksOf(Long projectId) {
        get(projectId);
        return tasks.findByProjectIdWithProject(projectId).stream().map(TaskMapper::toResponse).toList();
    }

    @Transactional                    // a write: needs a read-write transaction
    public ProjectResponse create(CreateProjectRequest request) {
        if (projects.existsByNameIgnoreCase(request.name().strip())) {
            throw new DuplicateException("A project named '" + request.name().strip() + "' already exists");
        }
        return TaskMapper.toResponse(projects.save(new Project(request.name().strip(), request.description())));
    }

    @Transactional
    public void delete(Long id) {
        projects.delete(get(id));       // ON DELETE CASCADE in V2 removes its tasks too
    }

    Project get(Long id) {
        return projects.findById(id).orElseThrow(() -> new NotFoundException("Project", id));
    }
}
