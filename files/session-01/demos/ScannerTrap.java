import java.util.Scanner;

// nextInt() leaves the Enter key in the input; the next nextLine() reads it.
public class ScannerTrap {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Age: ");
        int age = in.nextInt();
        in.nextLine();                 // FIX: consume the leftover Enter
                                       // (delete this line to see the bug)
        System.out.print("City: ");
        String city = in.nextLine();

        System.out.println(age + " years old, from \"" + city + "\"");
    }
}
