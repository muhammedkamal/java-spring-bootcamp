// int / int drops the decimals.
public class IntegerDivision {
    public static void main(String[] args) {
        System.out.println(5 / 2);     // 2
        System.out.println(5 / 2.0);   // 2.5
        System.out.println(5 % 2);     // 1

        int a = 7, b = 2;
        System.out.println(a / b);            // 3
        System.out.println(a / (double) b);   // 3.5

        // Real bug: average with ints
        int total = 448, count = 5;
        System.out.println("Wrong average: " + total / count);            // 89
        System.out.println("Right average: " + (double) total / count);   // 89.6
    }
}
