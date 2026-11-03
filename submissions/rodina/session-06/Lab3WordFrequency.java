import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Lab 3: Word frequency with a HashMap.
// countWords(text): lower-case, split on spaces, strip everything that is not a letter
//   (word.replaceAll("[^a-z]", "")), skip empty words, count each word.
// topN(counts, n): the n most common words; equal counts in alphabetical order.
//   Hint: copy counts.entrySet() into an ArrayList and sort it with a Comparator.
// Check (main below):
//   7 distinct words
//   cat: 3
//   dog: 0
//   the 4
//   cat 3
//   a 2
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
        // TODO
        return counts;
    }

    static List<Map.Entry<String, Integer>> topN(Map<String, Integer> counts, int n) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        // TODO: sort, then return the first n (entries.subList(0, ...))
        return entries;
    }
}
