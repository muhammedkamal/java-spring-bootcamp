import java.util.List;
import java.util.Map;

// Lab 2: Stream-based report over Order records (from the course plan).
// Each Order has a customer, a product, a quantity and a unit price.
// Check (main below):
//   total per customer: {Ali=850.0, Mona=310.0, Sara=2750.0}
//   revenue: 3910.0
//   top 3 products by revenue: [Laptop=2400.0, Phone=1050.0, Monitor=240.0]
//   biggest order: Sara bought 2 x Laptop = 2400.0
//   customers who bought a Phone: [Ali, Sara]
//   average order value: 651.6666666666666
public class Lab2OrderReport {
    record Order(String customer, String product, int quantity, double unitPrice) {
        double total() { return quantity * unitPrice; }
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("Sara", "Laptop", 2, 1200.0),
                new Order("Ali", "Phone", 1, 700.0),
                new Order("Mona", "Monitor", 2, 120.0),
                new Order("Ali", "Keyboard", 5, 30.0),
                new Order("Sara", "Phone", 1, 350.0),
                new Order("Mona", "Mouse", 7, 10.0));

        System.out.println("total per customer: " + totalPerCustomer(orders));
        System.out.println("revenue: " + revenue(orders));
        System.out.println("top 3 products by revenue: " + topProducts(orders, 3));
        System.out.println("biggest order: " + biggestOrder(orders));
        System.out.println("customers who bought a Phone: " + buyersOf(orders, "Phone"));
        System.out.println("average order value: " + averageOrderValue(orders));
    }

    // Customer -> sum of their order totals. Keys sorted (use TreeMap::new).
    static Map<String, Double> totalPerCustomer(List<Order> orders) {
        return Map.of(); // TODO: groupingBy + summingDouble
    }

    static double revenue(List<Order> orders) {
        return 0; // TODO
    }

    // The n products with the highest revenue, as "Product=revenue" strings, highest first.
    // Hint: group by product (summingDouble), then sort the entries by value descending, limit(n).
    static List<String> topProducts(List<Order> orders, int n) {
        return List.of(); // TODO
    }

    // "Sara bought 2 x Laptop = 2400.0"
    static String biggestOrder(List<Order> orders) {
        return ""; // TODO: max by total(), then format
    }

    // Distinct customer names, sorted.
    static List<String> buyersOf(List<Order> orders, String product) {
        return List.of(); // TODO
    }

    static double averageOrderValue(List<Order> orders) {
        return 0; // TODO: mapToDouble(Order::total).average().orElse(0)
    }
}
