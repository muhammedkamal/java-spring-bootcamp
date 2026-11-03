// Overloading: same name, different parameter lists.
public class Overloading {
    static int add(int a, int b) {
        return a + b;
    }

    static double add(double a, double b) {
        return a + b;
    }

    static int add(int a, int b, int c) {
        return a + b + c;
    }

    public static void main(String[] args) {
        System.out.println(add(2, 3));        // 5
        System.out.println(add(2.5, 3.5));    // 6.0
        System.out.println(add(1, 2, 3));     // 6
    }
}
