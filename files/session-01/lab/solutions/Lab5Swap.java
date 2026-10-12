public class Lab5Swap {
    public static void main(String[] args) {
        int a = 3;
        int b = 8;
        System.out.println("Before: a = " + a + ", b = " + b);

        // Part 1: with a temp variable
        int temp = a;
        a = b;
        b = temp;
        System.out.println("After:  a = " + a + ", b = " + b);

        // Part 2: without a temp variable (swaps them back)
        a = a + b;   // 11
        b = a - b;   // 8
        a = a - b;   // 3
        System.out.println("Again:  a = " + a + ", b = " + b);
    }
}
