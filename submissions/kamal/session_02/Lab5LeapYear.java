package kamal.session_02;

import java.util.Scanner;

// Lab 5: Read a year and say whether it is a leap year.
// Rule: divisible by 4, except centuries, unless divisible by 400.
// Check: 2024 yes, 1900 no, 2000 yes, 2026 no
public class Lab5LeapYear {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // TODO: read year
        System.out.print("Enter year: ");
        int year = in.nextInt();
        // TODO: one boolean expression using %, && and ||
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
            System.out.printf("%d yes, it is a leap year",year);
        }else {
            System.out.printf("%d no, it is not a leap year",year);

        }
        in.close();
    }
}
