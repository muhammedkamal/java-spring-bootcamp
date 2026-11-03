// Methods: package a task, give it a name, reuse it.
public class Methods {
    static int square(int x) {
        return x * x;
    }

    static double average(int a, int b, int c) {
        return (a + b + c) / 3.0;
    }

    static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }

    public static void main(String[] args) {
        greet("Ada");
        System.out.println(square(7));
        System.out.printf("%.2f%n", average(80, 90, 100));
        int result = square(square(2));   // calls can be nested
        System.out.println(result);       // 16
    }
}
