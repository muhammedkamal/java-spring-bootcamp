package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Priority;
import java.time.LocalDate;

// What the API returns. We decide exactly which fields the outside world sees.
public record TaskResponse(Long id, String title, String description, Priority priority, boolean done, LocalDate dueDate) {
}
