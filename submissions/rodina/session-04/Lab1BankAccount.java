// Lab 1: BankAccount with deposit and withdraw rules.
// Rules: deposit and withdraw must be positive; withdraw cannot exceed the balance.
//        Both return true when they worked, false when they were rejected.
// Check (main below): prints Ada: 150.00, false, true, false, 120.0
public class Lab1BankAccount {
    public static void main(String[] args) {
        BankAccount acc = (new BankAccount("ada", 100.0));
        System.out.println(acc);
        System.out.println(acc.withdraw(500));
        System.out.println(acc.withdraw(30));
        System.out.println(acc.deposit(-5));
        System.out.println(acc.getBalance());
    }
}
class BankAccount {
    private final String owner;
    private double balance;
    public BankAccount(String owner, double openingBalance) {
        this.owner = owner;
        if (openingBalance >= 0) {
            this.balance = openingBalance;
        } else {
            this.balance = 0;
        }
    }
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;

        }
        balance -= amount;
        return true;
    }
    public double getBalance() {
        return balance;
    }
    @Override
    public String toString() {
        return owner + ": " + String.format("%.2f", balance);
    }
}