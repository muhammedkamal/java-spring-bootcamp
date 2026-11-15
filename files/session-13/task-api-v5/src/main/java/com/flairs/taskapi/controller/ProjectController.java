package com.flairs.taskapi.controller;

import com.flairs.taskapi.dto.CreateProjectRequest;
import com.flairs.taskapi.dto.ProjectResponse;
import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import com.flairs.taskapi.security.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public List<ProjectResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return service.findAll(AuthUser.from(jwt));
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.findById(id, AuthUser.from(jwt));
    }

    // A nested resource: the tasks of one project
    @GetMapping("/{id}/tasks")
    public List<TaskResponse> tasks(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.tasksOf(id, AuthUser.from(jwt));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request,
                                                  @AuthenticationPrincipal Jwt jwt) {
        ProjectResponse saved = service.create(request, AuthUser.from(jwt));
        return ResponseEntity.created(URI.create("/api/projects/" + saved.id())).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, AuthUser.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
