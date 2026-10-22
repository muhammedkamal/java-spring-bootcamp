import java.time.LocalDate;

// Lab 4: Library System domain model. This is the start of your individual project.
// enum BookStatus { AVAILABLE, BORROWED }
// enum MemberType: STUDENT (max 2 loans, 14 days), STAFF (max 5 loans, 30 days). Give it fields and getters.
// Book(isbn, title, author): starts AVAILABLE; isAvailable(), markBorrowed(), markReturned();
//   toString() -> "Effective Java by Joshua Bloch [AVAILABLE]"
// Member(id, name, type): canBorrow(int currentLoans); toString() -> "#1 Mona (STUDENT, max 2 books)"
// Loan(book, member, borrowedOn): due date = borrowedOn + the member type's loan days; marks the book borrowed.
//   isOverdue(LocalDate today), returnBook(); toString() -> "Effective Java -> Mona, due 2026-11-01"
// A Loan HAS-A Book and HAS-A Member (composition, not inheritance).
// Check (main below):
//   Effective Java by Joshua Bloch [AVAILABLE]
//   #1 Mona (STUDENT, max 2 books)
//   true
//   Effective Java -> Mona, due 2026-11-01
//   false
//   false
//   true
//   true
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

// TODO: enum BookStatus
enum BookStatus { AVAILABLE, BORROWED }
// TODO: enum MemberType with maxLoans and loanDays
enum MemberType {
    STUDENT(2,14),STAFF(5,30);
    private final int MAXLOANS;
    private final int MAXDAYS;
    MemberType(int MAXLOANS, int MAXDAYS) {
        this.MAXLOANS = MAXLOANS;
        this.MAXDAYS = MAXDAYS;
    }
    int maxLoans() {
        return MAXLOANS;
    }

    int loanDays() {
        return MAXDAYS;
    }
}
// TODO: class Book
class Book{
    private String isbn;
    private String title;
    private String author;
    private BookStatus bookStatus = BookStatus.AVAILABLE;
    Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
    boolean isAvailable(){
        return bookStatus == BookStatus.AVAILABLE;
    }
    void markBorrowed(){
        bookStatus = BookStatus.BORROWED;
    }
    void markReturned(){
        bookStatus = BookStatus.AVAILABLE;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("%s by %s [%s]",title,author,bookStatus);
    }
}
// TODO: class Member
class Member{
    private int id;
    private String name;
    private MemberType type;
    Member(int id,String name,MemberType type){
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

    boolean canBorrow(int currentLoans){
        return  (currentLoans < type.maxLoans());
    }
    @Override
    public String toString() {
        return String.format("#%d %s (%s, max %d books)",id,name,type, type.maxLoans());
    }
}
// TODO: class Loan
class Loan{
    private Book book;
    private Member member;
    private LocalDate borrowedOn;
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
