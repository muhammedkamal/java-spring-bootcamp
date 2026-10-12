import java.util.Scanner;

// Validate input: keep asking until the value is acceptable.
public class InputLoop {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int age;
        do {
            System.out.print("Age (1-120): ");
            age = in.nextInt();
        } while (age < 1 || age > 120);
        System.out.println("Thanks, age = " + age);
        in.close();
    }
}
