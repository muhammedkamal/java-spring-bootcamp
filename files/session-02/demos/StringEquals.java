import java.util.Scanner;

// Trap: == compares references, equals() compares text.
public class StringEquals {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Password: ");
        String input = in.nextLine();

        if (input == "java25") {              // WRONG: may be false even when typed correctly
            System.out.println("== says: access granted");
        } else {
            System.out.println("== says: access denied");
        }

        if (input.equals("java25")) {         // RIGHT
            System.out.println("equals says: access granted");
        } else {
            System.out.println("equals says: access denied");
        }
        in.close();
    }
}
