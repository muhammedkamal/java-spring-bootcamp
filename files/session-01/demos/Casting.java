// Widening is automatic; narrowing needs an explicit cast and can lose data.
public class Casting {
    public static void main(String[] args) {
        double price = 9.99;
        int whole = (int) price;              // 9 (cut, not rounded)
        long big = whole;                     // widening, automatic
        long rounded = Math.round(price);     // 10

        System.out.println("whole   = " + whole);
        System.out.println("big     = " + big);
        System.out.println("rounded = " + rounded);
    }
}
