package com.flairs.taskapi.model;

import java.time.LocalDate;

// The internal model. It never goes straight to the client: the controller sends TaskResponse instead.
public record Task(Long id, String title, String description, Priority priority, boolean done, LocalDate dueDate) {

    public Task withId(Long newId) {
        return new Task(newId, title, description, priority, done, dueDate);
    }

    public Task withDone(boolean newDone) {
        return new Task(id, title, description, priority, newDone, dueDate);
    }
}
