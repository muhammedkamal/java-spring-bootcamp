// Lab 5: Swap the values of a and b.
// Part 1: use a temporary variable.
// Part 2 (bonus): swap without a temp variable (hint: + and -).
public class Lab5Swap {
    public static void main(String[] args) {
        int a = 3;
        int b = 8;
        System.out.println("Before: a = " + a + ", b = " + b);
        // TODO: swap a and b
        //1
        int c = a;
        a = b;
        b = c;
        System.out.println("After:  a = " + a + ", b = " + b);
    }
}
