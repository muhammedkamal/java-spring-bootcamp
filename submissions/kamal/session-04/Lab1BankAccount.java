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
    private String owner;
    private double balance;
    // TODO: constructor (owner, openingBalance), balance never below 0
    BankAccount(String owner, double openingBalance){
        this.owner = owner;
        this.balance = Math.max(openingBalance, 0);
    }
    // TODO: boolean deposit(double amount)
    boolean deposit(double amount){
        if (amount <= 0){
            return false;
        }
        balance += amount;
        return true;
    }
    // TODO: boolean withdraw(double amount)
    boolean withdraw(double amount){
        if (amount <= 0 || amount > balance){
            return false;
        }
        balance -= amount;
        return true;
    }
    // TODO: double getBalance()
    double getBalance(){
        return balance;
    }
    // TODO: toString() -> "Ada: 150.00"
    @Override
    public String toString(){
        return String.format("%s: %.2f", owner, balance);
    }
}
