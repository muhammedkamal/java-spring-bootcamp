package com.flairs.taskapi.dto;

import com.flairs.taskapi.model.Project;
import com.flairs.taskapi.model.Tag;
import com.flairs.taskapi.model.Task;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class TaskMapper {

    private TaskMapper() {
    }

    // Call this inside a transaction: it touches the lazy project and tags.
    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(), task.isDone(),
                task.getDueDate(), task.getEstimateHours(), task.getProject().getId(), task.getProject().getName(),
                task.getTags().stream().map(Tag::getName).collect(Collectors.toCollection(TreeSet::new)),
                task.getCreatedAt(), task.getUpdatedAt());
    }

    public static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getCreatedAt());
    }
}
