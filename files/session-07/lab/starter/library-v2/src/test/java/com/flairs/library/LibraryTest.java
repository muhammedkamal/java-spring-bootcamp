package com.flairs.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LibraryTest {
    private static final LocalDate DAY = LocalDate.of(2026, 1, 1);

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.addBook(new Book("111", "Dune", "Frank Herbert", 1965, "Sci-Fi"));
        library.addBook(new Book("222", "Emma", "Jane Austen", 1815, "Classic"));
        library.addBook(new Book("333", "Persuasion", "Jane Austen", 1817, "Classic"));
        library.addBook(new Book("444", "Neuromancer", "William Gibson", 1984, "Sci-Fi"));
        library.addMember(new Member(1, "Sara", MemberType.STUDENT));
        library.addMember(new Member(2, "Omar", MemberType.STAFF));
    }

    // ---- books ----

    @Test
    void addBookRejectsDuplicateIsbn() {
        LibraryException e = assertThrows(LibraryException.class,
                () -> library.addBook(new Book("111", "Other", "Someone", 2000, "Other")));
        assertEquals("A book with ISBN 111 already exists", e.getMessage());
    }

    @Test
    void bookRecordRejectsBlankIsbn() {
        assertThrows(IllegalArgumentException.class, () -> new Book(" ", "Title", "Author", 2000, "Genre"));
    }

    @Test
    void findBookReturnsBookWhenPresent() {
        assertEquals("Dune", library.findBook("111").orElseThrow().title());
    }

    @Test
    void findBookReturnsEmptyWhenMissing() {
        assertTrue(library.findBook("999").isEmpty());
    }

    // TODO: write at least 12 more tests so the project has 15 or more.
    // Ideas: sorting, search (try @ParameterizedTest + @CsvSource), titlesByAuthor, countByGenre, oldestBooks,
    // borrow (due date, unavailable book, loan limit, unknown book or member), returnBook, overdue.
}
