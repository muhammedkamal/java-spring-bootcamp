// interface: a contract. Any class can sign it with implements, even classes with different parents.
public class Interfaces {
    public static void main(String[] args) {
        PaymentMethod[] methods = { new CardPayment("4111"), new WalletPayment(50) };

        for (PaymentMethod m : methods) {
            boolean ok = m.pay(120);
            System.out.println(m.label() + " -> " + (ok ? "paid" : "declined"));
        }
        // Card ****4111 -> paid
        // Wallet (50.0 EGP) -> declined

        Receipt r = new CardPayment("5500");
        r.printReceipt(99.5);                   // Receipt: 99.5 EGP   (default method)
    }
}

interface PaymentMethod {
    boolean pay(double amount);                 // abstract: no body
    String label();
}

interface Receipt {
    default void printReceipt(double amount) {  // default: a body every class gets for free
        System.out.println("Receipt: " + amount + " EGP");
    }
}

class CardPayment implements PaymentMethod, Receipt {   // a class can implement many interfaces
    private final String lastDigits;

    CardPayment(String lastDigits) {
        this.lastDigits = lastDigits;
    }

    @Override
    public boolean pay(double amount) {         // interface methods are public
        return amount > 0;
    }

    @Override
    public String label() {
        return "Card ****" + lastDigits;
    }
}

class WalletPayment implements PaymentMethod {
    private double balance;

    WalletPayment(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean pay(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }

    @Override
    public String label() {
        return "Wallet (" + balance + " EGP)";
    }
}
