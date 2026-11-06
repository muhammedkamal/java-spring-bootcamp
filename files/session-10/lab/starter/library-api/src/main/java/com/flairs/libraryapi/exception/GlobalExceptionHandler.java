package com.flairs.libraryapi.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// TODO 4: turn exceptions into ProblemDetail answers.
//   BookNotFoundException   -> 404, title "Book not found"
//   DuplicateIsbnException  -> 409, title "Duplicate ISBN"
//   Validation failure      -> 400 with an "errors" map { field: message }
//     (override handleMethodArgumentNotValid, copy the pattern from the Task API)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
}
