// Lab 1: Print 1 to 30. Multiples of 3 print Fizz, of 5 print Buzz, of both print FizzBuzz.
// Check: 1 2 Fizz 4 Buzz Fizz 7 8 Fizz Buzz 11 Fizz 13 14 FizzBuzz ...
// Hint: test the "both" case first.
public class Lab1FizzBuzz {
    public static void main(String[] args) {
        // TODO: for loop from 1 to 30, one value per line
        for (int x = 1; x <= 30; x++){
            if (x % 5 == 0 && x % 3 == 0){
                System.out.println("FizzBuzz");
            } else if (x % 5 == 0 ) {
                System.out.println("Buzz");
            } else if (x % 3 == 0) {
                System.out.println("Fizz");
            }else {
                System.out.println(x);
            }
        }
    }
}
