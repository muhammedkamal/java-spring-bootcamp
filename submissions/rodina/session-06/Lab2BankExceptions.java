// Lab 2: BankAccount with a custom checked exception.
// InsufficientFundsException extends Exception:
//   constructor (owner, needed, available), message "Bob needs 1000.0 but has 350.0", getShortBy().
// BankAccount(owner, openingBalance): negative opening balance -> IllegalArgumentException.
//   deposit(amount): amount <= 0 -> IllegalArgumentException("Deposit must be positive: -5.0")
//   withdraw(amount) throws InsufficientFundsException; amount <= 0 -> IllegalArgumentException
//   static transfer(from, to, amount) throws InsufficientFundsException: if it fails, no money moves.
//   toString() -> "Ada: 50.00"
// Check (main below):
//   Ada: 50.00 | Bob: 350.00
//   Bob needs 1000.0 but has 350.0
//   Short by 650.0
//   Ada: 50.00 | Bob: 350.00
//   Deposit must be positive: -5.0


class InsufficientFundsException extends Exception {

    private String owner;

    private double needed;

    private double available;

    public InsufficientFundsException(

            String owner, double needed, double available) {

        super(owner + " needs " + needed + " but has " + available);

        this.owner = owner;

        this.needed = needed;

        this.available = available;

    }

    public double getShortBy() {

        return needed - available;

    }

}

class BankAccount {

    private String owner;

    private double balance;

    public BankAccount(String owner, double openingBalance) {

        if (openingBalance < 0) {

            throw new IllegalArgumentException(

                    "Opening balance cannot be negative");

        }

        this.owner = owner;

        this.balance = openingBalance;

    }

    public void deposit(double amount) {

        if (amount <= 0) {

            throw new IllegalArgumentException(

                    "Deposit must be positive: " + amount);

        }

        balance += amount;

    }

    public void withdraw(double amount)

            throws InsufficientFundsException {

        if (amount <= 0) {

            throw new IllegalArgumentException(

                    "Withdrawal must be positive: " + amount);

        }

        if (amount > balance) {

            throw new InsufficientFundsException(

                    owner, amount, balance);

        }

        balance -= amount;

    }

    public static void transfer(

            BankAccount from, BankAccount to, double amount)

            throws InsufficientFundsException {

        if (amount <= 0) {

            throw new IllegalArgumentException(

                    "Transfer amount must be positive: " + amount);

        }

        from.withdraw(amount);

        to.deposit(amount);

    }

    @Override

    public String toString() {

        return String.format("%s: %.2f", owner, balance);

    }

}

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
