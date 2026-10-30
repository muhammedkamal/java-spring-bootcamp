import java.util.HashSet;
import java.util.Set;

// A record is an immutable data class: the compiler writes the constructor, getters,
// equals, hashCode and toString for you.
public class Records {
    record Point(int x, int y) {}

    record Book(String isbn, String title, int year) {
        Book {                                          // compact constructor: validate
            if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("isbn is required");
            if (year < 0) throw new IllegalArgumentException("year must not be negative");
            title = title.trim();                       // can also tidy the values
        }

        Book(String isbn, String title) {               // extra constructor
            this(isbn, title, 2024);
        }

        boolean isClassic() { return year < 1950; }     // methods are fine
        static Book unknown() { return new Book("0", "Unknown", 0); }
    }

    public static void main(String[] args) {
        Point p = new Point(3, 4);
        System.out.println(p);                          // Point[x=3, y=4]
        System.out.println(p.x() + p.y());              // 7 (accessor is x(), not getX())
        System.out.println(p.equals(new Point(3, 4)));  // true (compares the values)

        Set<Point> set = new HashSet<>();
        set.add(new Point(1, 1));
        set.add(new Point(1, 1));
        System.out.println(set.size());                 // 1 (hashCode is written for us)

        Book b = new Book("111", "  Dune  ", 1965);
        System.out.println(b);                          // Book[isbn=111, title=Dune, year=1965]
        System.out.println(new Book("222", "Emma").year());     // 2024
        System.out.println(Book.unknown().isClassic());         // true

        try {
            new Book("", "No isbn", 2000);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());    // Caught: isbn is required
        }
        // Fields are final: there are no setters. To "change" a record, create a new one.
    }
}
