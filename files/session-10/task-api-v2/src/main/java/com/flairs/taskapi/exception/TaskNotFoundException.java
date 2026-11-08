package com.flairs.taskapi.exception;

// Unchecked on purpose (Session 6): the service throws it, the @RestControllerAdvice turns it into a 404.
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task " + id + " not found");
    }
}
