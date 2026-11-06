package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// PUT replaces the whole task, so every field is required except description and dueDate.
public record UpdateTaskRequest(
        @NotBlank(message = "title is required")
        @Size(max = 100, message = "title must be at most 100 characters")
        String title,

        @Size(max = 500, message = "description must be at most 500 characters")
        String description,

        @NotNull(message = "priority is required (LOW, MEDIUM or HIGH)")
        Priority priority,

        @NotNull(message = "done is required")
        Boolean done,

        @FutureOrPresent(message = "dueDate cannot be in the past")
        LocalDate dueDate) {
}
