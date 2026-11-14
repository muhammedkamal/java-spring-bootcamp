
package com.flairs.bookapi;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
        // *TODO: add 4 seed books (for example 1984 / George Orwell / 1949)*

        books.put(1L, new Book(1L, "1984", "George Orwell", 1949));
        books.put(2L, new Book(2L, "Animal Farm", "George Orwell", 1945));
        books.put(3L, new Book(3L, "The Great Gatsby", "F. Scott Fitzgerald", 1925));
        books.put(4L, new Book(4L, "To Kill a Mockingbird", "Harper Lee", 1960));

        nextId.set(5);
    }

    // *TODO: list(author, sortBy)*
    @GetMapping
    public List<Book> list(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String sortBy) {

        return books.values().stream()
                .filter(book -> author == null ||
                        book.author().toLowerCase().contains(author.toLowerCase()))
                .sorted("year".equals(sortBy)
                        ? Comparator.comparing(Book::year)
                        : Comparator.comparing(Book::id))
                .toList();
    }

    // *TODO: get(id)*
    @GetMapping("/{id}")
    public ResponseEntity<Book> get(@PathVariable Long id) {

        Book book = books.get(id);

        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(book);
    }

    // *TODO: count()*
    @GetMapping("/count")
    public Map<String, Integer> count() {

        return Map.of("count", books.size());
    }

    // *TODO: create(body)*
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book body) {

        Long id = nextId.getAndIncrement();

        Book newBook = new Book(
                id,
                body.title(),
                body.author(),
                body.year()
        );

        books.put(id, newBook);

        return ResponseEntity
                .created(URI.create("/api/books/" + id))
                .body(newBook);
    }

    // *TODO: replace(id, body)*
    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(
            @PathVariable Long id,
            @RequestBody Book body) {

        if (!books.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        Book updatedBook = new Book(
                id,
                body.title(),
                body.author(),
                body.year()
        );

        books.put(id, updatedBook);

        return ResponseEntity.ok(updatedBook);
    }

    // *TODO: delete(id)*
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!books.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        books.remove(id);

        return ResponseEntity.noContent().build();
    }
}
