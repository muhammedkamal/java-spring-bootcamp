// SOLID, the O: open for extension, closed for modification.
// Adding a new discount means a new class, not editing a growing if/else chain.
public class OpenClosed {
    public static void main(String[] args) {
        Checkout checkout = new Checkout();
        System.out.println(checkout.total(1000, new NoDiscount()));        // 1000.0
        System.out.println(checkout.total(1000, new PercentDiscount(10))); // 900.0
        System.out.println(checkout.total(1000, new FixedDiscount(150)));  // 850.0
    }
}

// Before: every new rule edits this method.
//   if (type.equals("percent")) ... else if (type.equals("fixed")) ... else if ...

interface Discount {
    double apply(double amount);
}

class NoDiscount implements Discount {
    @Override
    public double apply(double amount) {
        return amount;
    }
}

class PercentDiscount implements Discount {
    private final double percent;

    PercentDiscount(double percent) {
        this.percent = percent;
    }

    @Override
    public double apply(double amount) {
        return amount - amount * percent / 100;
    }
}

class FixedDiscount implements Discount {
    private final double value;

    FixedDiscount(double value) {
        this.value = value;
    }

    @Override
    public double apply(double amount) {
        return Math.max(0, amount - value);
    }
}

class Checkout {
    double total(double amount, Discount discount) {    // never changes again
        return discount.apply(amount);
    }
}
