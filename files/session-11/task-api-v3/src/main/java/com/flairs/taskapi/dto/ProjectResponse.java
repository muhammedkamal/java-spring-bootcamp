package com.flairs.taskapi.dto;

import java.time.Instant;

public record ProjectResponse(Long id, String name, String description, Instant createdAt) {
}
