import java.util.Scanner;

// Lab 4: Read a number of seconds and print hours, minutes and seconds.
// Check: 3725 -> 1 h 2 min 5 s
// Hint: use / and % with 3600 and 60.
public class Lab4TimeSplit {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter a number of seconds : ");
        int seconds = in.nextInt();
        int hours = seconds / 3600;
        int minutes =  (seconds % 3600) / 60 ;
        int second = seconds % 60;
        System.out.println(hours + " h " + minutes + " min " + second + " s ");
    }
}
