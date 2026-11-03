import java.util.Scanner;

public class Lab1Palindrome {
    static boolean isPalindrome(String text) {
        String clean = text.toLowerCase().replace(" ", "");
        int left = 0;
        int right = clean.length() - 1;
        while (left < right) {
            if (clean.charAt(left) != clean.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Text: ");
        String text = in.nextLine();
        System.out.println(isPalindrome(text) ? "Palindrome" : "Not a palindrome");
        in.close();
    }
}
