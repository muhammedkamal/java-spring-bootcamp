package com.flairs.notifications;

import java.time.Instant;

public record Notification(Long taskId, String message, Instant at) {

    static Notification from(TaskEvent event) {
        String message = switch (event.type()) {
            case "CREATED" -> "New task: " + event.title();
            case "COMPLETED" -> "Done: " + event.title();
            default -> event.type() + ": " + event.title();     // a new event type must not break us
        };
        return new Notification(event.taskId(), message, event.occurredAt());
    }
}
