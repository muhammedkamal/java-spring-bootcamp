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

// TODO: interface PaymentMethod
interface PaymentMethod{
    boolean pay(double amount);
    String name();
}
// TODO: class CardPayment implements PaymentMethod
class CardPayment implements PaymentMethod{
    private String brand;
    private double limit;
    private double used;
    CardPayment(String brand, double limit){
        this.brand = brand;
        this.limit = limit;
    }
    @Override
    public boolean pay(double amount) {
        double charge = amount + amount * 0.02;
        if (amount <= 0 || used + charge > limit){
            return false;
        }
        used += charge;
        return true;
    }
    double getUsed(){
        return used;
    }
    @Override
    public String name() {
        return "Card (" + brand + ")";
    }
}
// TODO: class WalletPayment implements PaymentMethod
class WalletPayment implements PaymentMethod{
    private String provider;
    private double balance;
    WalletPayment(String provider,double balance){
        this.provider = provider;
        this.balance = balance;
    }
    @Override
    public boolean pay(double amount) {
        if (amount <= 0 || balance < amount){
            return false;
        }
        balance -= amount;
        return true;
    }
    double getBalance(){
        return balance;
    }
    @Override
    public String name() {
        return "Wallet (" + provider +")";
    }
}
// TODO: class Checkout with static boolean pay(double amount, PaymentMethod[] methods)
class Checkout {
    static boolean pay(double amount, PaymentMethod[] methods) {
        for (PaymentMethod method : methods) {
            if (method.pay(amount)) {
                System.out.println(amount + " paid with " + method.name());
                return true;
            }
        }
        System.out.println(amount + " declined by all methods");
        return false;
        }
}