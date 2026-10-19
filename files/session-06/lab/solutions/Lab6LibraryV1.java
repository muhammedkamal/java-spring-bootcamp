import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Lab 6 (Week 3 deliverable): Library System v1. Console, collections, custom exceptions, menu UI.
// Reference solution. Builds on the Session 5 Lab 4 domain model.
public class Lab6LibraryV1 {
    public static void main(String[] args) {
        Library library = new Library();
        seed(library);

        Scanner in = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) List books  2) Search  3) Add book  4) Borrow  5) Return  6) Overdue  0) Exit");
            System.out.print("Choice: ");
            if (!in.hasNextLine()) break;
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> printAll(library.allBooks());
                    case "2" -> {
                        String text = ask(in, "Search: ");
                        List<Book> found = library.search(text);
                        if (found.isEmpty()) System.out.println("No match");
                        printAll(found);
                    }
                    case "3" -> {
                        String isbn = ask(in, "ISBN: ");
                        String title = ask(in, "Title: ");
                        String author = ask(in, "Author: ");
                        library.addBook(new Book(isbn, title, author));
                        System.out.println("Added");
                    }
                    case "4" -> {
                        String isbn = ask(in, "ISBN: ");
                        int memberId = Integer.parseInt(ask(in, "Member id: "));
                        Loan loan = library.borrow(isbn, memberId, LocalDate.now());
                        System.out.println("Borrowed: " + loan);
                    }
                    case "5" -> {
                        library.returnBook(ask(in, "ISBN: "));
                        System.out.println("Returned");
                    }
                    case "6" -> {
                        List<Loan> late = library.overdue(LocalDate.now());
                        if (late.isEmpty()) System.out.println("Nothing overdue");
                        printAll(late);
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option");
                }
            } catch (LibraryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: please type a number");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Bye");
    }

    static void printAll(List<?> items) {
        for (Object item : items) {
            System.out.println(item);
        }
    }

    static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    static void seed(Library library) {
        try {
            library.addBook(new Book("111", "Clean Code", "Robert Martin"));
            library.addBook(new Book("222", "Effective Java", "Joshua Bloch"));
            library.addBook(new Book("333", "Head First Java", "Kathy Sierra"));
            library.addMember(new Member(1, "Mona", MemberType.STUDENT));
            library.addMember(new Member(2, "Omar", MemberType.STAFF));
        } catch (LibraryException e) {
            throw new IllegalStateException(e);     // seed data is fixed, so this is a bug
        }
    }
}

// ---------- Exceptions ----------

class LibraryException extends Exception {
    LibraryException(String message) {
        super(message);
    }
}

class BookNotFoundException extends LibraryException {
    BookNotFoundException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}

class MemberNotFoundException extends LibraryException {
    MemberNotFoundException(int id) {
        super("No member with id " + id);
    }
}

class BookUnavailableException extends LibraryException {
    BookUnavailableException(Book book) {
        super(book.getTitle() + " is already borrowed");
    }
}

class LoanLimitException extends LibraryException {
    LoanLimitException(Member member) {
        super(member.getName() + " reached the limit of " + member.getType().maxLoans() + " books");
    }
}

class DuplicateBookException extends LibraryException {
    DuplicateBookException(String isbn) {
        super("A book with ISBN " + isbn + " already exists");
    }
}

// ---------- Service ----------

class Library {
    private final Map<String, Book> books = new HashMap<>();        // by ISBN
    private final Map<Integer, Member> members = new HashMap<>();   // by id
    private final List<Loan> loans = new ArrayList<>();

    void addBook(Book book) throws DuplicateBookException {
        if (books.containsKey(book.getIsbn())) {
            throw new DuplicateBookException(book.getIsbn());
        }
        books.put(book.getIsbn(), book);
    }

    void addMember(Member member) {
        members.put(member.getId(), member);
    }

    Book findBook(String isbn) throws BookNotFoundException {
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn);
        }
        return book;
    }

    Member findMember(int id) throws MemberNotFoundException {
        Member member = members.get(id);
        if (member == null) {
            throw new MemberNotFoundException(id);
        }
        return member;
    }

    List<Book> allBooks() {
        List<Book> list = new ArrayList<>(books.values());
        list.sort(Comparator.comparing(Book::getTitle));
        return list;
    }

    List<Book> search(String text) {
        String needle = text.toLowerCase();
        List<Book> found = new ArrayList<>();
        for (Book b : allBooks()) {
            if (b.getTitle().toLowerCase().contains(needle) || b.getAuthor().toLowerCase().contains(needle)) {
                found.add(b);
            }
        }
        return found;
    }

    int activeLoans(Member member) {
        int count = 0;
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.getMember().equals(member)) {
                count++;
            }
        }
        return count;
    }

    Loan borrow(String isbn, int memberId, LocalDate today) throws LibraryException {
        Book book = findBook(isbn);
        Member member = findMember(memberId);
        if (!book.isAvailable()) {
            throw new BookUnavailableException(book);
        }
        if (!member.canBorrow(activeLoans(member))) {
            throw new LoanLimitException(member);
        }
        Loan loan = new Loan(book, member, today);
        loans.add(loan);
        return loan;
    }

    void returnBook(String isbn) throws LibraryException {
        Book book = findBook(isbn);
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.getBook().equals(book)) {
                loan.returnBook();
                return;
            }
        }
        throw new LibraryException(book.getTitle() + " is not on loan");
    }

    List<Loan> overdue(LocalDate today) {
        List<Loan> late = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.isOverdue(today)) {
                late.add(loan);
            }
        }
        return late;
    }
}

// ---------- Domain model (from Session 5, Lab 4) ----------

enum BookStatus {
    AVAILABLE, BORROWED
}

enum MemberType {
    STUDENT(2, 14),
    STAFF(5, 30);

    private final int maxLoans;
    private final int loanDays;

    MemberType(int maxLoans, int loanDays) {
        this.maxLoans = maxLoans;
        this.loanDays = loanDays;
    }

    int maxLoans() {
        return maxLoans;
    }

    int loanDays() {
        return loanDays;
    }
}

class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private BookStatus status = BookStatus.AVAILABLE;

    Book(String isbn, String title, String author) {
        if (isbn.isBlank() || title.isBlank()) {
            throw new IllegalArgumentException("ISBN and title are required");
        }
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    String getIsbn() {
        return isbn;
    }

    String getTitle() {
        return title;
    }

    String getAuthor() {
        return author;
    }

    boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    void markBorrowed() {
        status = BookStatus.BORROWED;
    }

    void markReturned() {
        status = BookStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Book other && isbn.equals(other.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }

    @Override
    public String toString() {
        return isbn + "  " + title + " by " + author + " [" + status + "]";
    }
}

class Member {
    private final int id;
    private final String name;
    private final MemberType type;

    Member(int id, String name, MemberType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    MemberType getType() {
        return type;
    }

    boolean canBorrow(int currentLoans) {
        return currentLoans < type.maxLoans();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Member other && id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "#" + id + " " + name + " (" + type + ")";
    }
}

class Loan {
    private final Book book;
    private final Member member;
    private final LocalDate dueOn;
    private boolean returned;

    Loan(Book book, Member member, LocalDate borrowedOn) {
        this.book = book;
        this.member = member;
        this.dueOn = borrowedOn.plusDays(member.getType().loanDays());
        book.markBorrowed();
    }

    Book getBook() {
        return book;
    }

    Member getMember() {
        return member;
    }

    boolean isReturned() {
        return returned;
    }

    boolean isOverdue(LocalDate today) {
        return !returned && today.isAfter(dueOn);
    }

    void returnBook() {
        returned = true;
        book.markReturned();
    }

    @Override
    public String toString() {
        return book.getTitle() + " -> " + member.getName() + ", due " + dueOn;
    }
}
