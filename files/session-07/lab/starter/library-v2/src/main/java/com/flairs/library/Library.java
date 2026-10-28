package com.flairs.library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Library {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<Integer, Member> members = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();

    public void addBook(Book book) {
        if (books.putIfAbsent(book.isbn(), book) != null) {
            throw new LibraryException("A book with ISBN " + book.isbn() + " already exists");
        }
    }

    public void addMember(Member member) {
        members.put(member.id(), member);
    }

    public Optional<Book> findBook(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    // TODO: every method below. Use streams, Optional and records. The tests in LibraryTest describe the behaviour.

    // All books sorted by title.
    public List<Book> allBooksByTitle() {
        return List.of();
    }

    // Books whose title or author contains the text, ignoring case, sorted by title.
    public List<Book> search(String text) {
        return List.of();
    }

    // Author -> titles (sorted). Authors in alphabetical order (TreeMap::new).
    public Map<String, List<String>> titlesByAuthor() {
        return new TreeMap<>();
    }

    // Genre -> number of books. Genres in alphabetical order.
    public Map<String, Long> countByGenre() {
        return new TreeMap<>();
    }

    // The n oldest books, oldest first (ties by title).
    public List<Book> oldestBooks(int n) {
        return List.of();
    }

    // True if no active loan exists for this ISBN.
    public boolean isAvailable(String isbn) {
        return false;
    }

    public long activeLoans(int memberId) {
        return 0;
    }

    // Throws LibraryException when: the book or member does not exist ("No book with ISBN 999"),
    // the book is already borrowed ("Dune is already borrowed"), or the member is at the limit.
    // Due date = today + member.type().loanDays().
    public Loan borrow(String isbn, int memberId, LocalDate today) {
        throw new UnsupportedOperationException("TODO");
    }

    // Replaces the active loan with a returned copy. Throws LibraryException if the book is not on loan.
    public void returnBook(String isbn, LocalDate today) {
        throw new UnsupportedOperationException("TODO");
    }

    // Active loans whose due date is before today.
    public List<Loan> overdue(LocalDate today) {
        return List.of();
    }
}
