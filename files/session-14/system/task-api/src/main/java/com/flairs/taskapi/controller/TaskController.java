package com.flairs.taskapi.controller;

import com.flairs.taskapi.dto.CreateTaskRequest;
import com.flairs.taskapi.dto.PageResponse;
import com.flairs.taskapi.dto.PriorityCount;
import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.dto.UpdateTaskRequest;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.flairs.taskapi.security.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // Pageable is filled from ?page=0&size=5&sort=dueDate,asc  (page numbers start at 0)
    @Operation(summary = "Search tasks with paging and sorting, e.g. ?done=false&page=0&size=5&sort=dueDate,asc")
    @GetMapping
    public PageResponse<TaskResponse> list(@RequestParam(required = false) Boolean done,
                                           @RequestParam(required = false) Priority priority,
                                           @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                           @AuthenticationPrincipal Jwt jwt) {
        return service.search(AuthUser.from(jwt), done, priority, pageable);
    }

    @GetMapping("/overdue")
    public List<TaskResponse> overdue(@AuthenticationPrincipal Jwt jwt) {
        return service.overdue(AuthUser.from(jwt));
    }

    @GetMapping("/stats")
    public List<PriorityCount> stats(@AuthenticationPrincipal Jwt jwt) {
        return service.stats(AuthUser.from(jwt));
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.findById(id, AuthUser.from(jwt));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request,
                                               @AuthenticationPrincipal Jwt jwt) {
        TaskResponse saved = service.create(request, AuthUser.from(jwt));
        return ResponseEntity.created(URI.create("/api/tasks/" + saved.id())).body(saved);
    }

    @PutMapping("/{id}")
    public TaskResponse replace(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request,
                                @AuthenticationPrincipal Jwt jwt) {
        return service.update(id, request, AuthUser.from(jwt));
    }

    @PatchMapping("/{id}/complete")
    public TaskResponse complete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.complete(id, AuthUser.from(jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, AuthUser.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
