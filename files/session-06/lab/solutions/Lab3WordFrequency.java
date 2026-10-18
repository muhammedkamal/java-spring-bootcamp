import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Lab 3: Word frequency with a HashMap. Reference solution.
public class Lab3WordFrequency {
    public static void main(String[] args) {
        String text = "The cat and the hat. The cat sat! A hat, a cat, and THE end.";

        Map<String, Integer> counts = countWords(text);
        System.out.println(counts.size() + " distinct words");
        System.out.println("cat: " + counts.get("cat"));
        System.out.println("dog: " + counts.getOrDefault("dog", 0));

        for (Map.Entry<String, Integer> e : topN(counts, 3)) {
            System.out.println(e.getKey() + " " + e.getValue());
        }
    }

    static Map<String, Integer> countWords(String text) {
        Map<String, Integer> counts = new HashMap<>();
        for (String raw : text.toLowerCase().split("\\s+")) {
            String word = raw.replaceAll("[^a-z]", "");
            if (word.isEmpty()) {
                continue;
            }
            counts.put(word, counts.getOrDefault(word, 0) + 1);
        }
        return counts;
    }

    // Highest count first; equal counts in alphabetical order.
    static List<Map.Entry<String, Integer>> topN(Map<String, Integer> counts, int n) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int byCount = Integer.compare(b.getValue(), a.getValue());
            return byCount != 0 ? byCount : a.getKey().compareTo(b.getKey());
        });
        return entries.subList(0, Math.min(n, entries.size()));
    }
}
