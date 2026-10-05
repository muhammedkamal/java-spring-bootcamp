// Arithmetic, comparison, logical and shortcut operators.
public class Operators {
    public static void main(String[] args) {
        int a = 7, b = 3;
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));
        System.out.println("a / b = " + (a / b));   // 2 (integer division)
        System.out.println("a % b = " + (a % b));   // 1 (remainder)

        System.out.println("a > b  : " + (a > b));
        System.out.println("a == b : " + (a == b));
        System.out.println("a != b : " + (a != b));

        boolean hasTicket = true, isAdult = false;
        System.out.println("can enter: " + (hasTicket && isAdult));
        System.out.println("either:    " + (hasTicket || isAdult));
        System.out.println("not adult: " + !isAdult);

        int x = 5;
        x += 3;   // x = x + 3 -> 8
        x++;      // x = x + 1 -> 9
        System.out.println("x = " + x);
    }
}
