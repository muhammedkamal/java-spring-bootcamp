//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Scanner;
//
//// Lab 6 (Week 3 deliverable): Library System v1.
//// Console app, collections, custom exceptions, menu UI. Put it in its own GitHub repo with a README.
////
//// 1. Paste your domain model from Session 5 Lab 4 (BookStatus, MemberType, Book, Member, Loan) at the bottom.
////    Add getAuthor() to Book, and equals/hashCode (Book by ISBN, Member by id).
//// 2. Exceptions: LibraryException extends Exception, and these extend LibraryException:
////    BookNotFoundException, MemberNotFoundException, BookUnavailableException,
////    LoanLimitException, DuplicateBookException.
//// 3. Library: books in a HashMap by ISBN, members in a HashMap by id, loans in an ArrayList.
//// 4. Menu: 1) List books  2) Search  3) Add book  4) Borrow  5) Return  6) Overdue  0) Exit
////    Catch LibraryException in the menu loop and print "Error: " + message. The app must never crash
////    on bad input (try a letter where a number is expected).
//// Sample run:
////   Choice: 4, ISBN: 222, Member id: 1  -> Borrowed: Effective Java -> Mona, due 2026-11-01
////   Choice: 4, ISBN: 222, Member id: 2  -> Error: Effective Java is already borrowed
////   Choice: 4, ISBN: 999, Member id: 1  -> Error: No book with ISBN 999
//public class Lab6LibraryV1 {
//    public static void main(String[] args) {
//        Library library = new Library();
//        // TODO: add 3 books and 2 members (one STUDENT, one STAFF)
//
//        Scanner in = new Scanner(System.in);
//        boolean running = true;
//        while (running) {
//            System.out.println("1) List books  2) Search  3) Add book  4) Borrow  5) Return  6) Overdue  0) Exit");
//            System.out.print("Choice: ");
//            String choice = in.nextLine().trim();
//            // TODO: switch on choice inside a try; catch LibraryException and NumberFormatException
//        }
//    }
//}
//
//// TODO: exceptions
//
//class Library {
//    private final Map<String, Book> books = new HashMap<>();
//    private final Map<Integer, Member> members = new HashMap<>();
//    private final List<Loan> loans = new ArrayList<>();
//
//    // TODO: void addBook(Book book) throws DuplicateBookException
//    // TODO: void addMember(Member member)
//    // TODO: Book findBook(String isbn) throws BookNotFoundException
//    // TODO: Member findMember(int id) throws MemberNotFoundException
//    // TODO: List<Book> allBooks()                  -> sorted by title
//    // TODO: List<Book> search(String text)         -> title or author contains text, ignoring case
//    // TODO: int activeLoans(Member member)
//    // TODO: Loan borrow(String isbn, int memberId, LocalDate today) throws LibraryException
//    // TODO: void returnBook(String isbn) throws LibraryException
//    // TODO: List<Loan> overdue(LocalDate today)
//}
//
//// TODO: paste the domain model here
