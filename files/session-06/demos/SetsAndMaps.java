import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// HashSet: no duplicates. HashMap: look up a value by its key.
public class SetsAndMaps {
    public static void main(String[] args) {
        Set<String> tags = new HashSet<>();
        tags.add("java");
        tags.add("spring");
        System.out.println(tags.add("java"));       // false: already there
        System.out.println(tags.size());            // 2
        System.out.println(tags.contains("spring"));// true

        Map<String, Integer> stock = new HashMap<>();
        stock.put("pen", 10);
        stock.put("book", 3);
        stock.put("pen", 12);                       // same key: replaces the value
        System.out.println(stock.get("pen"));       // 12
        System.out.println(stock.get("cup"));       // null
        System.out.println(stock.getOrDefault("cup", 0));   // 0

        // Word count: the most common map pattern
        String text = "to be or not to be";
        Map<String, Integer> counts = new TreeMap<>();      // TreeMap keeps keys sorted
        for (String word : text.split(" ")) {
            counts.put(word, counts.getOrDefault(word, 0) + 1);
        }
        System.out.println(counts);                 // {be=2, not=1, or=1, to=2}

        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() > 1) {
                System.out.println(e.getKey() + " x" + e.getValue());   // be x2, to x2
            }
        }
    }
}
