import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.function.Consumer;

// A functional interface has exactly one abstract method. A lambda is an implementation of it.
public class FunctionalInterfaces {
    @FunctionalInterface
    interface Discount {
        double apply(double price);
    }

    public static void main(String[] args) {
        Predicate<String> isLong = s -> s.length() > 4;                 // T -> boolean
        Function<String, Integer> length = String::length;              // T -> R
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;    // (T, U) -> R
        Supplier<String> greeting = () -> "Hello";                      // () -> T
        Consumer<String> printer = s -> System.out.println("> " + s);   // T -> void
        UnaryOperator<String> shout = s -> s.toUpperCase() + "!";       // T -> T

        System.out.println(isLong.test("banana"));      // true
        System.out.println(length.apply("kiwi"));       // 4
        System.out.println(add.apply(2, 3));            // 5
        printer.accept(greeting.get());                 // > Hello
        System.out.println(shout.apply("java"));        // JAVA!

        // Combine small pieces
        Predicate<String> isShort = isLong.negate();
        Predicate<String> startsWithK = s -> s.startsWith("k");
        System.out.println(isShort.and(startsWithK).test("kiwi"));      // true
        Function<String, Integer> shoutedLength = shout.andThen(length);
        System.out.println(shoutedLength.apply("java"));                // 5

        // Our own functional interface
        Discount tenPercent = p -> p * 0.9;
        Discount flat50 = p -> Math.max(0, p - 50);
        System.out.println(checkout(200, tenPercent));  // 180.0
        System.out.println(checkout(200, flat50));      // 150.0
    }

    static double checkout(double price, Discount discount) {
        return discount.apply(price);
    }
}
