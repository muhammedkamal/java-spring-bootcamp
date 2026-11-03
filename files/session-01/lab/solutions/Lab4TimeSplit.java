import java.util.Scanner;

public class Lab4TimeSplit {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Seconds: ");
        int totalSeconds = in.nextInt();

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        System.out.println(hours + " h " + minutes + " min " + seconds + " s");
    }
}
