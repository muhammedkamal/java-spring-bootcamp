// Lab 2: BankAccount with a custom checked exception. Reference solution.
public class Lab2BankExceptions {
    public static void main(String[] args) {
        BankAccount a = new BankAccount("Ada", 500);
        BankAccount b = new BankAccount("Bob", 100);

        try {
            a.withdraw(200);
            BankAccount.transfer(a, b, 250);
            System.out.println(a + " | " + b);
            BankAccount.transfer(b, a, 1000);
            System.out.println("never printed");
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
            System.out.println("Short by " + e.getShortBy());
        }
        System.out.println(a + " | " + b);

        try {
            a.deposit(-5);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}

class InsufficientFundsException extends Exception {
    private final double shortBy;

    InsufficientFundsException(String owner, double needed, double available) {
        super(owner + " needs " + needed + " but has " + available);
        this.shortBy = needed - available;
    }

    double getShortBy() {
        return shortBy;
    }
}

class BankAccount {
    private final String owner;
    private double balance;

    BankAccount(String owner, double openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }
        this.owner = owner;
        this.balance = openingBalance;
    }

    void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive: " + amount);
        }
        balance += amount;
    }

    void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be positive: " + amount);
        }
        if (amount > balance) {
            throw new InsufficientFundsException(owner, amount, balance);
        }
        balance -= amount;
    }

    // Withdraw first: if it fails, nothing has moved.
    static void transfer(BankAccount from, BankAccount to, double amount) throws InsufficientFundsException {
        from.withdraw(amount);
        to.deposit(amount);
    }

    double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f", owner, balance);
    }
}
