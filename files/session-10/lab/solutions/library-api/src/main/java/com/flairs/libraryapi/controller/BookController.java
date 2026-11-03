package com.flairs.libraryapi.controller;

import com.flairs.libraryapi.dto.BookResponse;
import com.flairs.libraryapi.dto.CreateBookRequest;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.service.BookService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> list(@RequestParam(required = false) String author) {
        return service.findAll(author).stream().map(BookController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) {
        return toResponse(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody CreateBookRequest request) {
        Book saved = service.create(toBook(request));
        return ResponseEntity.created(URI.create("/api/books/" + saved.id())).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public BookResponse replace(@PathVariable Long id, @Valid @RequestBody CreateBookRequest request) {
        return toResponse(service.update(id, toBook(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static Book toBook(CreateBookRequest r) {
        return new Book(null, r.title().strip(), r.author().strip(), r.isbn(), r.year(), r.copies());
    }

    private static BookResponse toResponse(Book b) {
        return new BookResponse(b.id(), b.title(), b.author(), b.isbn(), b.year(), b.copies());
    }
}
