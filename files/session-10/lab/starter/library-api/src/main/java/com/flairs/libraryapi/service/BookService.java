package com.flairs.libraryapi.service;

import com.flairs.libraryapi.exception.BookNotFoundException;
import com.flairs.libraryapi.exception.DuplicateIsbnException;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// TODO 2: implement the service. It holds the business rules and knows nothing about HTTP.
@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
        repository.save(new Book(null, "1984", "George Orwell", "9780451524935", 1949, 3));
        repository.save(new Book(null, "Clean Code", "Robert C. Martin", "9780132350884", 2008, 2));
    }

    // all books; if author is not null keep only books whose author contains it (ignore case)
    public List<Book> findAll(String author) {
        throw new UnsupportedOperationException("TODO");
    }

    // throw BookNotFoundException when the id does not exist
    public Book findById(Long id) {
        throw new UnsupportedOperationException("TODO");
    }

    // throw DuplicateIsbnException when the ISBN is already used
    public Book create(Book book) {
        throw new UnsupportedOperationException("TODO");
    }

    // 404 if the id does not exist. 409 if the new ISBN belongs to ANOTHER book. Otherwise save with the same id.
    public Book update(Long id, Book book) {
        throw new UnsupportedOperationException("TODO");
    }

    // throw BookNotFoundException when nothing was deleted
    public void delete(Long id) {
        throw new UnsupportedOperationException("TODO");
    }
}
