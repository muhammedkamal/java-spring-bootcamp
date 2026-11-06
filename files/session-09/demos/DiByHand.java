// The problem Spring solves, in one file. Run: java demos/DiByHand.java
//
// Part 1: OrderServiceTight creates its own dependencies with `new`. It is welded to EmailSender.
// Part 2: OrderService receives its dependency through the constructor (dependency injection).
// Part 3: main() plays the role of Spring: it creates the objects and wires them together.
// Spring does exactly Part 3 for you. That is Inversion of Control (IoC).
public class DiByHand {

    interface NotificationSender {
        void send(String to, String message);
    }

    static class EmailSender implements NotificationSender {
        public void send(String to, String message) {
            System.out.println("[EMAIL to " + to + "] " + message);
        }
    }

    static class SmsSender implements NotificationSender {
        public void send(String to, String message) {
            System.out.println("[SMS to " + to + "] " + message);
        }
    }

    // ---- Part 1: tight coupling ----
    static class OrderServiceTight {
        private final EmailSender sender = new EmailSender();   // decided here, forever

        void placeOrder(String customer) {
            sender.send(customer, "Order received");
        }
        // To use SMS you must edit this class. To test it you cannot avoid sending a real email.
    }

    // ---- Part 2: loose coupling with constructor injection ----
    static class OrderService {
        private final NotificationSender sender;                 // depends on the interface only

        OrderService(NotificationSender sender) {                // the dependency comes from outside
            this.sender = sender;
        }

        void placeOrder(String customer) {
            sender.send(customer, "Order received");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Part 1: tight coupling ---");
        new OrderServiceTight().placeOrder("ali@example.com");
        // [EMAIL to ali@example.com] Order received

        System.out.println("--- Part 2 and 3: we wire the objects by hand ---");
        OrderService withEmail = new OrderService(new EmailSender());
        OrderService withSms = new OrderService(new SmsSender());
        withEmail.placeOrder("ali@example.com");
        withSms.placeOrder("01012345678");
        // [EMAIL to ali@example.com] Order received
        // [SMS to 01012345678] Order received

        System.out.println("--- Testing without a real sender: a fake one-liner ---");
        OrderService forTest = new OrderService((to, msg) -> System.out.println("[FAKE] " + to + ": " + msg));
        forTest.placeOrder("test");
        // [FAKE] test: Order received
    }
}
