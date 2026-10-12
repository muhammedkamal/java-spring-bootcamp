import java.util.Scanner;

// Menu loop skeleton: the shape of the Week 1 deliverable (Lab 6).
public class FloorMenu {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("1) Say hello  2) Say bye  0) Exit");
            System.out.print("Choice: ");
            int choice = in.nextInt();
            switch (choice) {
                case 1 -> System.out.println("Hello!");
                case 2 -> System.out.println("Bye!");
                case 0 -> running = false;
                default -> System.out.println("Unknown option");
            }
        }
        System.out.println("Goodbye");
        in.close();
    }
}
