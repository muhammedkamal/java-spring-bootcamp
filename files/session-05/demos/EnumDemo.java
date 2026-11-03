// enum: a type with a fixed list of values. Safer than magic strings like "SHIPPED".
public class EnumDemo {
    public static void main(String[] args) {
        OrderStatus s = OrderStatus.SHIPPED;

        System.out.println(s);                  // SHIPPED
        System.out.println(s.label());          // On the way
        System.out.println(s == OrderStatus.SHIPPED);   // true: enums compare with ==

        String advice = switch (s) {
            case NEW -> "Waiting for payment";
            case PAID, SHIPPED -> "Track your order";
            case DELIVERED -> "Leave a review";
            case CANCELLED -> "Refund in 3 days";
        };
        System.out.println(advice);             // Track your order

        for (OrderStatus each : OrderStatus.values()) {
            System.out.print(each.ordinal() + ":" + each + " ");
        }
        System.out.println();                   // 0:NEW 1:PAID 2:SHIPPED 3:DELIVERED 4:CANCELLED

        OrderStatus parsed = OrderStatus.valueOf("PAID");
        System.out.println(parsed.label());     // Paid
    }
}

enum OrderStatus {
    NEW("New"),
    PAID("Paid"),
    SHIPPED("On the way"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String label;                 // enums can have fields

    OrderStatus(String label) {                 // and a constructor (always private)
        this.label = label;
    }

    String label() {
        return label;
    }
}
