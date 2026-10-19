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
// TODO: enum MemberType with maxLoans and loanDays
// TODO: class Book
// TODO: class Member
// TODO: class Loan
