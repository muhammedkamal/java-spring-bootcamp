package com.flairs.bookapi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Lab 2: build the Book API. Same pattern as the Task API from class.
// Run it with ./mvnw spring-boot:run (or mvn spring-boot:run) and test with Postman or requests.http.
//
// Implement these endpoints under /api/books:
//   GET    /api/books                    all books, sorted by id
//   GET    /api/books?author=orwell      only books whose author contains the text (ignore case)
//   GET    /api/books?sortBy=year        sorted by year instead of id (any other value: sort by id)
//   GET    /api/books/{id}               one book, 404 if it does not exist
//   GET    /api/books/count              {"count": 4}   (hint: return a Map<String, Integer>)
//   POST   /api/books                    201 Created + Location header, id assigned by the server
//   PUT    /api/books/{id}               replace the book, 404 if it does not exist
//   DELETE /api/books/{id}               204 No Content, 404 if it does not exist
//
// Hints:
//  * "/count" and "/{id}" both match GET /api/books/count. Spring picks the more specific one, "/count".
//  * Put the seed data in the constructor, like TaskController.
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public BookController() {
        // TODO: add 4 seed books (for example 1984 / George Orwell / 1949)
    }

    // TODO: list(author, sortBy)
    // TODO: get(id)
    // TODO: count()
    // TODO: create(body)
    // TODO: replace(id, body)
    // TODO: delete(id)
}
