
import java.util.Scanner;

// Lab 2: Read n and print its multiplication table from 1 to 10.
// Check: n = 7 -> 7 x 1 = 7 ... 7 x 10 = 70
public class Lab2MultiplicationTable {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a Number");
        int number = input.nextInt();
        for (int i = 1; i <= 10; i++) {
            System.out.println(number + "x" + i + "=" + (number * i));
        }
        input.close();
    }
}
