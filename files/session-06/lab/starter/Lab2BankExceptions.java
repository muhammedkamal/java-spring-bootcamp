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

// TODO: class InsufficientFundsException extends Exception
// TODO: class BankAccount
