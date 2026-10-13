public class Lab1BankAccount {
    public static void main(String[] args) {
        BankAccount acc = new BankAccount("Ada", 100.0);
        acc.deposit(50);
        System.out.println(acc);               // Ada: 150.00
        System.out.println(acc.withdraw(500)); // false, not enough money
        System.out.println(acc.withdraw(30));  // true
        System.out.println(acc.deposit(-5));   // false, must be positive
        System.out.println(acc.getBalance());  // 120.0
    }
}

class BankAccount {
    private final String owner;
    private double balance;

    BankAccount(String owner, double openingBalance) {
        this.owner = owner;
        this.balance = Math.max(openingBalance, 0);
    }

    boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }

    boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return owner + ": " + String.format("%.2f", balance);
    }
}
