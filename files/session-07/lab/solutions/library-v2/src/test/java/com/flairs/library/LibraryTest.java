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

    @Test
    void allBooksAreSortedByTitle() {
        List<String> titles = library.allBooksByTitle().stream().map(Book::title).toList();
        assertEquals(List.of("Dune", "Emma", "Neuromancer", "Persuasion"), titles);
    }

    @ParameterizedTest
    @CsvSource({ "dune, 1", "AUSTEN, 2", "ma, 2", "zzz, 0" })
    void searchMatchesTitleOrAuthorIgnoringCase(String text, int expected) {
        assertEquals(expected, library.search(text).size());
    }

    // ---- reports (streams) ----

    @Test
    void titlesByAuthorGroupsAndSorts() {
        Map<String, List<String>> result = library.titlesByAuthor();
        assertEquals(List.of("Emma", "Persuasion"), result.get("Jane Austen"));
        assertEquals(3, result.size());
    }

    @Test
    void countByGenreCountsEachGenre() {
        assertEquals(Map.of("Classic", 2L, "Sci-Fi", 2L), library.countByGenre());
    }

    @Test
    void oldestBooksReturnsOldestFirst() {
        List<String> titles = library.oldestBooks(2).stream().map(Book::title).toList();
        assertEquals(List.of("Emma", "Persuasion"), titles);
    }

    // ---- borrowing ----

    @Test
    void borrowSetsDueDateFromMemberType() {
        Loan loan = library.borrow("111", 1, DAY);
        assertEquals(DAY.plusDays(14), loan.dueOn());
        Loan staffLoan = library.borrow("222", 2, DAY);
        assertEquals(DAY.plusDays(30), staffLoan.dueOn());
    }

    @Test
    void borrowedBookIsNotAvailable() {
        library.borrow("111", 1, DAY);
        assertFalse(library.isAvailable("111"));
        assertTrue(library.isAvailable("222"));
    }

    @Test
    void borrowingAnAlreadyBorrowedBookFails() {
        library.borrow("111", 1, DAY);
        LibraryException e = assertThrows(LibraryException.class, () -> library.borrow("111", 2, DAY));
        assertEquals("Dune is already borrowed", e.getMessage());
    }

    @Test
    void studentCannotBorrowMoreThanTwoBooks() {
        library.borrow("111", 1, DAY);
        library.borrow("222", 1, DAY);
        assertThrows(LibraryException.class, () -> library.borrow("333", 1, DAY));
        assertEquals(2, library.activeLoans(1));
    }

    @Test
    void borrowUnknownBookOrMemberFails() {
        assertThrows(LibraryException.class, () -> library.borrow("999", 1, DAY));
        assertThrows(LibraryException.class, () -> library.borrow("111", 99, DAY));
    }

    // ---- returning and overdue ----

    @Test
    void returnBookMakesItAvailableAgain() {
        library.borrow("111", 1, DAY);
        library.returnBook("111", DAY.plusDays(3));
        assertTrue(library.isAvailable("111"));
        assertEquals(0, library.activeLoans(1));
    }

    @Test
    void returnBookThatIsNotOnLoanFails() {
        assertThrows(LibraryException.class, () -> library.returnBook("111", DAY));
    }

    @Test
    void overdueListsOnlyLateActiveLoans() {
        library.borrow("111", 1, DAY);                      // due 15 Jan
        library.borrow("222", 2, DAY);                      // due 31 Jan
        List<Loan> late = library.overdue(DAY.plusDays(20));
        assertEquals(1, late.size());
        assertEquals("111", late.get(0).isbn());
    }

    @Test
    void returnedLoanIsNeverOverdue() {
        library.borrow("111", 1, DAY);
        library.returnBook("111", DAY.plusDays(40));
        assertTrue(library.overdue(DAY.plusDays(60)).isEmpty());
    }
}
