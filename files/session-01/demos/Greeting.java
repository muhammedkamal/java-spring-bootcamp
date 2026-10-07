import java.util.Scanner;

// Reading input from the keyboard.
public class Greeting {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Your name: ");
        String name = in.nextLine();

        System.out.print("Your age: ");
        int age = in.nextInt();

        System.out.println("Hi " + name
            + ", next year you'll be " + (age + 1));
        in.close();
    }
}
