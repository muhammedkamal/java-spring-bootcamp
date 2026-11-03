// Lab 1: BankAccount with deposit and withdraw rules.
// Rules: deposit and withdraw must be positive; withdraw cannot exceed the balance.
//        Both return true when they worked, false when they were rejected.
// Check (main below): prints Ada: 150.00, false, true, false, 120.0
public class Lab1BankAccount {
    public static void main(String[] args) {
        BankAccount acc = new BankAccount("Ada", 100.0);
        acc.deposit(50);
        System.out.println(acc);
        System.out.println(acc.withdraw(500));
        System.out.println(acc.withdraw(30));
        System.out.println(acc.deposit(-5));
        System.out.println(acc.getBalance());
    }
}

class BankAccount {
    // TODO: private fields owner and balance
    // TODO: constructor (owner, openingBalance), balance never below 0
    // TODO: boolean deposit(double amount)
    // TODO: boolean withdraw(double amount)
    // TODO: double getBalance()
    // TODO: toString() -> "Ada: 150.00"
}
