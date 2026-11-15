package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Priority;

public record PriorityCount(Priority priority, long count) {
}
