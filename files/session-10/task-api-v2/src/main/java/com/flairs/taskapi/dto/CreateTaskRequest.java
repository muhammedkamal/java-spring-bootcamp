package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// What the client may send when creating a task. No id, no done: the client cannot set those.
// The annotations are the rules. @Valid in the controller makes Spring check them.
public record CreateTaskRequest(
        @NotBlank(message = "title is required")
        @Size(max = 100, message = "title must be at most 100 characters")
        String title,

        @Size(max = 500, message = "description must be at most 500 characters")
        String description,

        @NotNull(message = "priority is required (LOW, MEDIUM or HIGH)")
        Priority priority,

        @FutureOrPresent(message = "dueDate cannot be in the past")
        LocalDate dueDate) {
}
