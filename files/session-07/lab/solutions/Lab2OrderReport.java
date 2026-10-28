import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.stream.Collectors;

// Lab 2 (reference solution): Stream-based report over Order records (from the course plan).
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
        return orders.stream().collect(Collectors.groupingBy(Order::customer, TreeMap::new, Collectors.summingDouble(Order::total)));
    }

    static double revenue(List<Order> orders) {
        return orders.stream().mapToDouble(Order::total).sum();
    }

    // The n products with the highest revenue, as "Product=revenue" strings, highest first.
    // Hint: group by product (summingDouble), then sort the entries by value descending, limit(n).
    static List<String> topProducts(List<Order> orders, int n) {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::product, Collectors.summingDouble(Order::total)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .map(e -> e.getKey() + "=" + e.getValue())
                .toList();
    }

    // "Sara bought 2 x Laptop = 2400.0"
    static String biggestOrder(List<Order> orders) {
        Order o = orders.stream().max(Comparator.comparingDouble(Order::total)).orElseThrow();
        return o.customer() + " bought " + o.quantity() + " x " + o.product() + " = " + o.total();
    }

    // Distinct customer names, sorted.
    static List<String> buyersOf(List<Order> orders, String product) {
        return orders.stream().filter(o -> o.product().equals(product))
                .map(Order::customer).distinct().sorted().toList();
    }

    static double averageOrderValue(List<Order> orders) {
        return orders.stream().mapToDouble(Order::total).average().orElse(0);
    }
}
