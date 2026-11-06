package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Task;

// Hand-written mapping, so you see what happens. Big projects generate this with MapStruct.
public final class TaskMapper {

    private TaskMapper() {
    }

    public static Task toTask(CreateTaskRequest request) {
        return new Task(null, request.title().strip(), request.description(), request.priority(), false, request.dueDate());
    }

    public static Task toTask(Long id, UpdateTaskRequest request) {
        return new Task(id, request.title().strip(), request.description(), request.priority(), request.done(), request.dueDate());
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(task.id(), task.title(), task.description(), task.priority(), task.done(), task.dueDate());
    }
}
