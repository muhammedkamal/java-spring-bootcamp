import java.util.Scanner;

public class Lab2VowelCounter {
    static int countVowels(String text) {
        int count = 0;
        for (char c : text.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Text: ");
        String text = in.nextLine();
        System.out.println("Vowels: " + countVowels(text));
        in.close();
    }
}
