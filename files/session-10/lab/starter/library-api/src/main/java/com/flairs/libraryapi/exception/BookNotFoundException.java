package com.flairs.libraryapi.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book " + id + " not found");
    }
}
