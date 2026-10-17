import java.util.Scanner;

// Lab 1: Is the text a palindrome? Ignore case and spaces.
// Check: "A man a plan a canal Panama" yes, "Java" no
// Hint: compare charAt(left) with charAt(right) and move both inward.

public class Lab1Palindrome {
    static boolean isPalindrome(String text) {
        text = text.replace(" ", "").toLowerCase();
        int left = 0;
        int right = text.length() - 1;
        while (left < right) {
            if (text.charAt(left) != text.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String text = in.nextLine();
        System.out.println(isPalindrome(text));
        in.close();
    }
}