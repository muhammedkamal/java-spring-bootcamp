import java.util.List;
import java.util.Optional;

// Optional<T> says "there may be no value" in the type, instead of returning null.
public class OptionalDemo {
    record Book(String title, String author) {}

    static final List<Book> BOOKS = List.of(new Book("Dune", "Herbert"), new Book("Emma", "Austen"));

    static Optional<Book> findByTitle(String title) {
        return BOOKS.stream().filter(b -> b.title().equalsIgnoreCase(title)).findFirst();
    }

    public static void main(String[] args) {
        Optional<Book> dune = findByTitle("dune");
        Optional<Book> missing = findByTitle("Ulysses");

        System.out.println(dune.isPresent());                   // true
        System.out.println(missing.isEmpty());                  // true
        System.out.println(dune.get().author());                // Herbert (get() on an empty Optional throws)

        System.out.println(missing.map(Book::author).orElse("unknown"));       // unknown
        System.out.println(dune.map(Book::author).orElse("unknown"));          // Herbert
        System.out.println(missing.map(Book::title).orElseGet(() -> "built only when needed"));

        dune.ifPresent(b -> System.out.println("Found " + b.title()));          // Found Dune
        missing.ifPresentOrElse(
                b -> System.out.println("Found " + b.title()),
                () -> System.out.println("No such book"));                       // No such book

        try {
            missing.orElseThrow(() -> new IllegalArgumentException("No book titled Ulysses"));
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());                    // Caught: No book titled Ulysses
        }

        // Rule: use Optional as a return type. Not as a field, not as a parameter.
        System.out.println(Optional.ofNullable(null).isPresent());              // false
        // Optional.of(null) throws NullPointerException: use ofNullable when it might be null
    }
}
