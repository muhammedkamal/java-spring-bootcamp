package com.flairs.libraryapi.service;

import com.flairs.libraryapi.exception.BookNotFoundException;
import com.flairs.libraryapi.exception.DuplicateIsbnException;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
        repository.save(new Book(null, "1984", "George Orwell", "9780451524935", 1949, 3));
        repository.save(new Book(null, "Clean Code", "Robert C. Martin", "9780132350884", 2008, 2));
    }

    public List<Book> findAll(String author) {
        return repository.findAll().stream()
                .filter(b -> author == null || b.author().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book) {
        if (repository.existsByIsbn(book.isbn())) {
            throw new DuplicateIsbnException(book.isbn());
        }
        return repository.save(book);
    }

    public Book update(Long id, Book book) {
        Book existing = findById(id);
        // changing the ISBN to one that belongs to ANOTHER book is a conflict
        if (!existing.isbn().equals(book.isbn()) && repository.existsByIsbn(book.isbn())) {
            throw new DuplicateIsbnException(book.isbn());
        }
        return repository.save(book.withId(id));
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new BookNotFoundException(id);
        }
    }
}
