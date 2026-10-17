import java.util.Scanner;

// Lab 3: Count the words in a sentence. Extra spaces must not add words.
// Check: "  the quick   brown fox " -> 4, empty line -> 0
// Hint: trim() first, then split("\\s+").
public class Lab3WordCounter {
    // TODO: static int countWords(String text)

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read a line, print the count
        System.out.print("Wrtie a text: ");
        String text = in.nextLine();
        System.out.println(countWords(text));
        in.close();
    }
    public static int countWords(String text){
        String[] textArr = text.trim().split("\\s+");
        int count = 0;
        for(String a : textArr){
            count++;
        }
        return count;
    }
}
