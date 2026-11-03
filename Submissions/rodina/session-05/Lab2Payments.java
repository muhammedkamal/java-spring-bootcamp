// Lab 2: PaymentMethod interface with CardPayment and WalletPayment.
// interface PaymentMethod { boolean pay(double amount); String name(); }
// CardPayment(brand, limit): adds a 2% fee; the charge (amount + fee) must fit in what is left of the limit.
//   name() -> "Card (Visa)". getUsed() returns how much of the limit is used.
// WalletPayment(provider, balance): no fee; cannot go below 0.
//   name() -> "Wallet (Vodafone Cash)". getBalance() returns the balance.
// Both reject amounts <= 0.
// Checkout.pay(amount, methods): try each method in order, stop at the first that works.
//   Prints "250.0 EGP paid with Wallet (Vodafone Cash)" or "900.0 EGP declined by all methods".
// Check (main below):
//   250.0 EGP paid with Wallet (Vodafone Cash)
//   250.0 EGP paid with Card (Visa)
//   900.0 EGP declined by all methods
//   50.0
//   255.0

public class Lab2Payments {
    public static void main(String[] args) {
        CardPayment card = new CardPayment("Visa", 1000);
        WalletPayment wallet = new WalletPayment("Vodafone Cash", 300);
        PaymentMethod[] order = { wallet, card };

        Checkout.pay(250, order);
        Checkout.pay(250, order);
        Checkout.pay(900, order);
        System.out.println(wallet.getBalance());
        System.out.println(card.getUsed());
    }
}

interface PaymentMethod {
    boolean pay(double amount);

    String name();
}

class CardPayment implements PaymentMethod {
    static final double FEE_RATE = 0.02;
    private final String brand;
    private final double limit;
    private double used;

    CardPayment(String brand, double limit) {
        this.brand = brand;
        this.limit = limit;
    }

    @Override
    public boolean pay(double amount) {
        double charge = amount + amount * FEE_RATE;
        if (amount <= 0 || used + charge > limit) {
            return false;
        }
        used += charge;
        return true;
    }

    @Override
    public String name() {
        return "Card (" + brand + ")";
    }

    double getUsed() {
        return used;
    }
}

class WalletPayment implements PaymentMethod {
    private final String provider;
    private double balance;

    WalletPayment(String provider, double balance) {
        this.provider = provider;
        this.balance = balance;
    }

    @Override
    public boolean pay(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    @Override
    public String name() {
        return "Wallet (" + provider + ")";
    }

    double getBalance() {
        return balance;
    }
}

class Checkout {

    static boolean pay(double amount, PaymentMethod[] methods) {
        for (PaymentMethod m : methods) {
            if (m.pay(amount)) {
                System.out.println(amount + " EGP paid with " + m.name());
                return true;
            }
        }
        System.out.println(amount + " EGP declined by all methods");
        return false;
    }
}
