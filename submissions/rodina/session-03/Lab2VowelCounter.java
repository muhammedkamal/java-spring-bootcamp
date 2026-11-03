import java.util.Scanner;

// Lab 2: Count the vowels (a e i o u, either case) in a line of text.
// Check: "Hello World Education" -> 8
// Hint: "aeiou".indexOf(c) >= 0 tells you whether c is a vowel.
public class Lab2VowelCounter {
    static int countVowels(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toLowerCase(text.charAt(i));
            if ("aeiou".indexOf(c) >= 0) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a text");
        String text = in.nextLine();
        System.out.println(countVowels(text));
        // TODO: read a line, print the count
        in.close();
    }
}
