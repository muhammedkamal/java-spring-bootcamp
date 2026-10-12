import java.util.Scanner;

public class lab4Timesplit {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter sec ");
        int totalsec = input.nextInt();
        int hours = totalsec / 3600;
        int remainingsec = totalsec % 3600;
        int minutes = remainingsec / 60;
        int seconds = remainingsec % 60;
        System.out.println("Hours=" + hours);
        System.out.println("Minutes=" + minutes);
        System.out.println("Seconds=" + seconds);


    }
}
