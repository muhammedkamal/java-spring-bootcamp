import java.util.Scanner;

public class Lab3WordCounter {
    static int countWords(String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Sentence: ");
        String text = in.nextLine();
        System.out.println("Words: " + countWords(text));
        in.close();
    }
}
