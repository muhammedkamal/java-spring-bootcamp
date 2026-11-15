package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Priority;
import java.io.Serializable;

// Serializable since Session 14: the Redis cache stores it with Java serialization
public record PriorityCount(Priority priority, long count) implements Serializable {
}
