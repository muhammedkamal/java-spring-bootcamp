
import java.util.Scanner;

public class Temperature {
    public static void main(String[]args){
       Scanner num = new Scanner (System.in);
       System.out.print("enter Temperature in cel =");
       double Cel = num.nextDouble();
       double fahrenheit = Cel *9/5+32;
       System.out.println("Fahrenheit = " +fahrenheit);
    }
}
