// var lets the compiler infer the type; final makes a variable constant.
public class VarAndFinal {
    public static void main(String[] args) {
        var count = 10;          // int
        var message = "Hi";      // String
        final double VAT = 0.14;

        double price = 200;
        System.out.println(message + ", count = " + count);
        System.out.println("Price with VAT: " + (price + price * VAT));

        // VAT = 0.15;           // error: cannot assign a value to final variable VAT
    }
}
