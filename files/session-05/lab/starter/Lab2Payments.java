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
// TODO: class CardPayment implements PaymentMethod
// TODO: class WalletPayment implements PaymentMethod
// TODO: class Checkout with static boolean pay(double amount, PaymentMethod[] methods)
