// Lab 1: Print 1 to 30. Multiples of 3 print Fizz, of 5 print Buzz, of both print FizzBuzz.
// Check: 1 2 Fizz 4 Buzz Fizz 7 8 Fizz Buzz 11 Fizz 13 14 FizzBuzz ...
// Hint: test the "both" case first.

public class lab1FizzBuzz {
    public static void main(String[] args) {
        for (int i = 1; i <= 30; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                System.out.println("FizzBzz");
            } else if (i % 3 == 0) {
                System.out.println("Fizz");

            } else if (i % 5 == 0) {
                System.out.println("Buzz");
            } else {
                System.out.println(i);

            }


        }


    }
}
