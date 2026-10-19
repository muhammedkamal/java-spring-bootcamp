// Composition: build a class from parts (HAS-A) instead of extending (IS-A).
// The part is an interface, so it can be swapped without touching OrderService.
public class Composition {
    public static void main(String[] args) {
        OrderService email = new OrderService(new EmailNotifier());
        OrderService sms = new OrderService(new SmsNotifier());

        email.placeOrder("Mona", 250);          // [email to Mona] Order of 250.0 EGP confirmed
        sms.placeOrder("Omar", 90);             // [SMS to Omar] Order of 90.0 EGP confirmed
    }
}

interface Notifier {
    void send(String to, String message);
}

class EmailNotifier implements Notifier {
    @Override
    public void send(String to, String message) {
        System.out.println("[email to " + to + "] " + message);
    }
}

class SmsNotifier implements Notifier {
    @Override
    public void send(String to, String message) {
        System.out.println("[SMS to " + to + "] " + message);
    }
}

class OrderService {
    private final Notifier notifier;            // OrderService HAS-A Notifier

    OrderService(Notifier notifier) {           // the part is handed in from outside
        this.notifier = notifier;
    }

    void placeOrder(String customer, double amount) {
        // ... save the order ...
        notifier.send(customer, "Order of " + amount + " EGP confirmed");
    }
}
// In Week 5, Spring does the "hand it in from outside" part for you.
