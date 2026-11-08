package com.flairs.taskapi.controller;

import com.flairs.taskapi.dto.CreateTaskRequest;
import com.flairs.taskapi.dto.TaskMapper;
import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.dto.UpdateTaskRequest;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Task;
import com.flairs.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
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

// The web layer: translate HTTP to a service call and back. No business rules here.
// Thin controller = a few lines per method.
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Create, read, update and delete tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "List tasks, optionally filtered by done and priority")
    @GetMapping
    public List<TaskResponse> list(@RequestParam(required = false) Boolean done,
                                   @RequestParam(required = false) Priority priority) {
        return service.findAll(done, priority).stream().map(TaskMapper::toResponse).toList();
    }

    @Operation(summary = "Get one task by id")
    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable @Positive(message = "id must be positive") Long id) {
        return TaskMapper.toResponse(service.findById(id));
    }

    // @Valid + @RequestBody: Spring validates the body BEFORE the method runs.
    // If a rule fails, the method never runs and MethodArgumentNotValidException is thrown.
    @Operation(summary = "Create a task")
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        Task saved = service.create(TaskMapper.toTask(request));
        return ResponseEntity.created(URI.create("/api/tasks/" + saved.id())).body(TaskMapper.toResponse(saved));
    }

    @Operation(summary = "Replace a task")
    @PutMapping("/{id}")
    public TaskResponse replace(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        return TaskMapper.toResponse(service.update(id, TaskMapper.toTask(id, request)));
    }

    @Operation(summary = "Mark a task as done")
    @PatchMapping("/{id}/complete")
    public TaskResponse complete(@PathVariable Long id) {
        return TaskMapper.toResponse(service.complete(id));
    }

    @Operation(summary = "Delete a task")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
