import java.util.ArrayList;
import java.util.List;

// ArrayList: an array that grows by itself.
public class ListsDemo {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();     // List is the interface, ArrayList the class
        names.add("Mona");
        names.add("Omar");
        names.add("Sara");
        names.add(1, "Ali");                        // insert at index 1

        System.out.println(names);                  // [Mona, Ali, Omar, Sara]
        System.out.println(names.size());           // 4
        System.out.println(names.get(2));           // Omar
        System.out.println(names.contains("Sara")); // true

        names.remove("Ali");
        names.set(0, "Mona K.");
        for (String n : names) {
            System.out.print(n + "; ");
        }
        System.out.println();                       // Mona K.; Omar; Sara;

        List<Integer> marks = new ArrayList<>(List.of(90, 45, 70, 30));
        marks.removeIf(m -> m < 50);                // safe way to remove while looping
        System.out.println(marks);                  // [90, 70]

        List<String> fixed = List.of("a", "b");     // List.of is read-only
        // fixed.add("c");                          // UnsupportedOperationException
        // List<int> nums;                          // does not compile: use Integer
    }
}
