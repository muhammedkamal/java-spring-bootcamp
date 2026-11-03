import java.util.Scanner;

// switch with the arrow form (no fall-through, no break needed).
public class SwitchDemo {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Day number (1-7, 1 = Monday): ");
        int day = in.nextInt();

        String type = switch (day) {
            case 6, 7 -> "Weekend";         // Saturday, Sunday
            case 1, 2, 3, 4, 5 -> "Work day";
            default -> "Not a day";
        };
        System.out.println(type);
        in.close();
    }
}
