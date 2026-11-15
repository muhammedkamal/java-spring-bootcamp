package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Priority;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// PUT replaces the editable fields. The project and tags are not changed here.
public record UpdateTaskRequest(
        @NotBlank(message = "title is required") @Size(max = 100, message = "title must be at most 100 characters") String title,
        @Size(max = 500, message = "description must be at most 500 characters") String description,
        @NotNull(message = "priority is required (LOW, MEDIUM or HIGH)") Priority priority,
        @NotNull(message = "done is required") Boolean done,
        LocalDate dueDate,
        @Min(value = 1, message = "estimateHours must be 1 to 100") @Max(value = 100, message = "estimateHours must be 1 to 100") Integer estimateHours) {
}
