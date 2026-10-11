package Submissions;

import java.util.Scanner;

public class lap2Temperature {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("enter Temperature in cel =");
        double cel = input.nextDouble();
        double fahrenheit = cel * 9 / 5 + 32;
        System.out.println("Fahrenheit = " + fahrenheit);
    }
}
