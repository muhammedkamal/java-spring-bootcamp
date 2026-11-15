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
import com.flairs.taskapi.repository.UserRepository;
import com.flairs.taskapi.security.AuthUser;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Session 12: every method gets the caller (AuthUser). A USER sees only their own projects, an ADMIN sees all.
@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final UserRepository users;

    public ProjectService(ProjectRepository projects, TaskRepository tasks, UserRepository users) {
        this.projects = projects;
        this.tasks = tasks;
        this.users = users;
    }

    public List<ProjectResponse> findAll(AuthUser caller) {
        List<Project> found = caller.isAdmin()
                ? projects.findAll(Sort.by("name"))
                : projects.findByOwnerIdOrderByName(caller.id());
        return found.stream().map(TaskMapper::toResponse).toList();
    }

    public ProjectResponse findById(Long id, AuthUser caller) {
        return TaskMapper.toResponse(getVisible(id, caller));
    }

    public List<TaskResponse> tasksOf(Long projectId, AuthUser caller) {
        getVisible(projectId, caller);
        return tasks.findByProjectIdWithProject(projectId).stream().map(TaskMapper::toResponse).toList();
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request, AuthUser caller) {
        String name = request.name().strip();
        if (projects.existsByOwnerIdAndNameIgnoreCase(caller.id(), name)) {
            throw new DuplicateException("You already have a project named '" + name + "'");
        }
        // getReferenceById: a proxy with only the id, no SELECT. Enough to fill the owner_id column.
        Project project = new Project(name, request.description(), users.getReferenceById(caller.id()));
        return TaskMapper.toResponse(projects.save(project));
    }

    @Transactional
    public void delete(Long id, AuthUser caller) {
        projects.delete(getVisible(id, caller));
    }

    // The ownership rule in ONE place. Someone else's project answers 404, not 403:
    // a 403 would confirm that the id exists.
    Project getVisible(Long id, AuthUser caller) {
        Project project = projects.findById(id).orElseThrow(() -> new NotFoundException("Project", id));
        if (!caller.isAdmin() && !project.isOwnedBy(caller.id())) {
            throw new NotFoundException("Project", id);
        }
        return project;
    }
}
