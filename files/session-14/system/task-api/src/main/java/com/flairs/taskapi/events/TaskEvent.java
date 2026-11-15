package com.flairs.taskapi.events;

import java.time.Instant;

// NEW in Session 14: what the Task API tells the rest of the system. This is a CONTRACT:
// other services (notification-service) read it, so add fields freely but never rename or remove one.
public record TaskEvent(Type type, Long taskId, String title, Long ownerId, Instant occurredAt) {

    public enum Type { CREATED, COMPLETED }

    // task.created, task.completed: consumers subscribe with a pattern such as "task.#"
    public String routingKey() {
        return "task." + type.name().toLowerCase();
    }
}
