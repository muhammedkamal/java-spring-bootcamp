import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

// A method reference is a lambda that only calls one existing method.
public class MethodReferences {
    record Book(String title, int year) {
        static boolean isClassic(Book b) { return b.year() < 1950; }
        String label() { return title + " (" + year + ")"; }
    }

    public static void main(String[] args) {
        List<String> names = List.of("sara", "omar", "mona");

        names.forEach(System.out::println);                 // instance method of a given object
        // same as: names.forEach(n -> System.out.println(n));

        List<Integer> lengths = names.stream().map(String::length).toList();   // any object of a type
        System.out.println(lengths);                        // [4, 4, 4]

        List<Book> books = List.of(new Book("Dune", 1965), new Book("1984", 1949));
        System.out.println(books.stream().filter(Book::isClassic).toList());   // static method
        // [Book[title=1984, year=1949]]

        Function<String, Integer> parse = Integer::parseInt;                    // static method
        System.out.println(parse.apply("42") + 1);                              // 43

        Supplier<StringBuilder> maker = StringBuilder::new;                     // constructor
        System.out.println(maker.get().append("new").append("!"));              // new!

        books.stream().map(Book::label).forEach(System.out::println);
        // Dune (1965)
        // 1984 (1949)
    }
}
