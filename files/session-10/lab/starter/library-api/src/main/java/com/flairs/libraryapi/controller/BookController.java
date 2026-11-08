package com.flairs.libraryapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO 3: build the controller under /api/books. Keep it thin: map the DTO, call the service, return the result.
//   GET    /api/books?author=     -> List<BookResponse>
//   GET    /api/books/{id}        -> BookResponse
//   POST   /api/books             -> 201 + Location, body validated with @Valid
//   PUT    /api/books/{id}        -> BookResponse, body validated with @Valid
//   DELETE /api/books/{id}        -> 204
// Write two private helpers: Book toBook(CreateBookRequest) and BookResponse toResponse(Book).
// Inject BookService through the constructor.
@RestController
@RequestMapping("/api/books")
public class BookController {
}
