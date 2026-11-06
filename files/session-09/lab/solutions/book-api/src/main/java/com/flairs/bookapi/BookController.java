package com.flairs.bookapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Lab 2 solution.
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public BookController() {
        save(new Book(null, "1984", "George Orwell", 1949));
        save(new Book(null, "Animal Farm", "George Orwell", 1945));
        save(new Book(null, "Clean Code", "Robert C. Martin", 2008));
        save(new Book(null, "Effective Java", "Joshua Bloch", 2001));
    }

    private Book save(Book book) {
        long id = nextId.getAndIncrement();
        Book saved = new Book(id, book.title(), book.author(), book.year());
        books.put(id, saved);
        return saved;
    }

    @GetMapping
    public List<Book> list(@RequestParam(required = false) String author,
                           @RequestParam(required = false) String sortBy) {
        List<Book> result = new ArrayList<>(books.values());
        if (author != null) {
            result.removeIf(b -> !b.author().toLowerCase().contains(author.toLowerCase()));
        }
        Comparator<Book> order = "year".equals(sortBy)
                ? Comparator.comparing(Book::year)
                : Comparator.comparing(Book::id);
        result.sort(order);
        return result;
    }

    @GetMapping("/count")
    public Map<String, Integer> count() {
        return Map.of("count", books.size());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> get(@PathVariable Long id) {
        Book book = books.get(id);
        return book == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book body) {
        Book saved = save(body);
        return ResponseEntity.created(URI.create("/api/books/" + saved.id())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(@PathVariable Long id, @RequestBody Book body) {
        if (!books.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        Book updated = new Book(id, body.title(), body.author(), body.year());
        books.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return books.remove(id) == null ? ResponseEntity.notFound().build() : ResponseEntity.noContent().build();
    }
}
