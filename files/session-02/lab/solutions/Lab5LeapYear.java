import java.util.Scanner;

public class Lab5LeapYear {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Year: ");
        int year = in.nextInt();
        boolean leap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
        System.out.println(year + (leap ? " is a leap year" : " is not a leap year"));
        in.close();
    }
}
