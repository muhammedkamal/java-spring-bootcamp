import java.util.Locale;
import java.util.Scanner;

// Lab 2: Count the vowels (a e i o u, either case) in a line of text.
// Check: "Hello World Education" -> 8
// Hint: "aeiou".indexOf(c) >= 0 tells you whether c is a vowel.
public class Lab2VowelCounter {
    // TODO: static int countVowels(String text)

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read a line, print the count
        System.out.print("Write a text: ");
        String text = in.nextLine();
        System.out.println(countVowels(text));
        in.close();
    }
    public static int countVowels(String text){
        String vowels = "aeiou";
        int count = 0;
        text = text.toLowerCase();
        for (int i = 0; i <= text.length() - 1; i++ ){
            for (int j = 0; j <= vowels.length() - 1; j++){
                if (text.charAt(i) == vowels.charAt(j)){
                    count++;
                }
            }
        }
        return count;
    }
}
