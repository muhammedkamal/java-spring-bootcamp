package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Priority;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

// Flat on purpose: projectId and projectName, not the whole Project entity (no endless JSON loops).
public record TaskResponse(Long id, String title, String description, Priority priority, boolean done, LocalDate dueDate,
                           Integer estimateHours, Long projectId, String projectName, Set<String> tags,
                           Instant createdAt, Instant updatedAt) {
}
