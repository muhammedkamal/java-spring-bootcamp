// Custom exceptions: a name that says exactly what went wrong, plus the data to explain it.
public class CustomExceptions {
    public static void main(String[] args) {
        Account acc = new Account(100);
        try {
            acc.withdraw(30);
            acc.withdraw(500);
            System.out.println("never printed");
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());         // Need 500.0, have 70.0
            System.out.println("Short by " + e.getShortBy());   // Short by 430.0
        }
    }
}

// extends Exception -> checked: callers must handle it
class InsufficientFundsException extends Exception {
    private final double shortBy;

    InsufficientFundsException(double needed, double available) {
        super("Need " + needed + ", have " + available);
        this.shortBy = needed - available;
    }

    double getShortBy() {
        return shortBy;
    }
}

class Account {
    private double balance;

    Account(double balance) {
        this.balance = balance;
    }

    void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");     // a caller bug
        }
        if (amount > balance) {
            throw new InsufficientFundsException(amount, balance);            // a business rule
        }
        balance -= amount;
    }
}
// Rule of thumb: extends RuntimeException for programming mistakes,
// extends Exception when the caller can do something sensible about it.
