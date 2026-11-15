package com.flairs.taskapi.controller;

import com.flairs.taskapi.dto.CreateProjectRequest;
import com.flairs.taskapi.dto.ProjectResponse;
import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProjectResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    // A nested resource: the tasks of one project
    @GetMapping("/{id}/tasks")
    public List<TaskResponse> tasks(@PathVariable Long id) {
        return service.tasksOf(id);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/projects/" + saved.id())).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
