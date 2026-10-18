import java.util.Scanner;

// Lab 3: Count the words in a sentence. Extra spaces must not add words.
// Check: "  the quick   brown fox " -> 4, empty line -> 0
// Hint: trim() first, then split("\\s+").
public class Lab3WordCounter {
    static int countWords(String text) {
        text = text.trim();
        if (text.isEmpty()) {
            return 0;
        }
        String[] words = text.split("\\s+");
        return words.length;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a text");
        String text = in.nextLine();

        System.out.println(countWords(text));
        // TODO: read a line, print the count
        in.close();
    }
}
