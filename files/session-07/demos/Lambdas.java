import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// A lambda is a short anonymous function: (parameters) -> body
public class Lambdas {
    public static void main(String[] args) {
        // Before Java 8: an anonymous class just to sort by length
        List<String> words = new ArrayList<>(List.of("pear", "fig", "banana", "kiwi"));
        words.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(a.length(), b.length());
            }
        });
        System.out.println(words);      // [fig, pear, kiwi, banana]

        // Same thing as a lambda
        words.sort((a, b) -> b.length() - a.length());
        System.out.println(words);      // [banana, pear, kiwi, fig]

        // One parameter: no brackets needed. Block body: braces and return.
        words.forEach(w -> System.out.print(w.toUpperCase() + " "));
        System.out.println();           // BANANA PEAR KIWI FIG

        words.removeIf(w -> w.length() <= 3);
        System.out.println(words);      // [banana, pear, kiwi]

        // A lambda can use a local variable only if it is effectively final
        int minLength = 5;
        words.removeIf(w -> w.length() < minLength);
        System.out.println(words);      // [banana]
        // minLength = 6;               // would break the line above: not effectively final
    }
}
