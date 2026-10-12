import java.util.Scanner;

// Lab 5: Read a year and say whether it is a leap year.
// Rule: divisible by 4, except centuries, unless divisible by 400.
// Check: 2024 yes, 1900 no, 2000 yes, 2026 no
public class Lab5LeapYear {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter a year: ");
        int year = input.nextInt();
        if (year % 400 == 0) {
            System.out.println("Leap year");
        } else if (year % 100 == 0) {
            System.out.println("Not a leap year");
        } else if (year % 4 == 0) {
            System.out.println("Leap year");
        } else {
            System.out.println("Not a leap year");
        }
        input.close();
    }
}