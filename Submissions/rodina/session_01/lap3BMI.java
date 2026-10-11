package Submissions;

import java.util.Scanner;

public class lap3BMI {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.print("Enter weight in kg: ");
        double weight = input.nextDouble();
        System.out.print("Enter height in meters:");
        double height = input.nextDouble();
        double bmi = weight / (height * height);
        System.out.println("BMI = " + bmi);

    }
}
