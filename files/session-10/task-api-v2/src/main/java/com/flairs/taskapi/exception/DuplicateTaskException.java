package com.flairs.taskapi.exception;

public class DuplicateTaskException extends RuntimeException {

    public DuplicateTaskException(String title) {
        super("A task titled '" + title + "' already exists");
    }
}
