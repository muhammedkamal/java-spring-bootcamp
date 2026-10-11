// Lab 1: Print your name, track and city inside a box of * characters.
// Example output:
// ****************************
// *  Name : Mona Ali         *
// *  Track: Java Backend     *
// *  City : Cairo            *
// ****************************
public class Lab1PersonalCard {
    public static void main(String[] args) {
        String name = "Mona Ali",track = "Java Backend",city = "Cairo";
        System.out.println("********************************");
        System.out.printf("*  %-27s *%n", "Name : " + name);
        System.out.printf("*  %-27s *%n", "Track: " + track);
        System.out.printf("*  %-27s *%n", "City : " + city);
        System.out.println("********************************");


    }
}
