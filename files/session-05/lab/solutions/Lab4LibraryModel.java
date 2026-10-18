import java.time.LocalDate;

// Lab 4: Library System domain model (start of the individual project). Reference solution.
public class Lab4LibraryModel {
    public static void main(String[] args) {
        Book book = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        Member mona = new Member(1, "Mona", MemberType.STUDENT);
        LocalDate today = LocalDate.of(2026, 10, 18);

        System.out.println(book);
        System.out.println(mona);
        System.out.println(mona.canBorrow(1));

        Loan loan = new Loan(book, mona, today);
        System.out.println(loan);
        System.out.println(book.isAvailable());
        System.out.println(loan.isOverdue(today.plusDays(10)));
        System.out.println(loan.isOverdue(today.plusDays(20)));

        loan.returnBook();
        System.out.println(book.isAvailable());
    }
}

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
    public String toString() {
        return title + " by " + author + " [" + status + "]";
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
    public String toString() {
        return "#" + id + " " + name + " (" + type + ", max " + type.maxLoans() + " books)";
    }
}

class Loan {
    private final Book book;                        // a Loan HAS-A Book
    private final Member member;                    // and HAS-A Member
    private final LocalDate borrowedOn;
    private final LocalDate dueOn;
    private boolean returned;

    Loan(Book book, Member member, LocalDate borrowedOn) {
        this.book = book;
        this.member = member;
        this.borrowedOn = borrowedOn;
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
