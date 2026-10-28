import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Lab 3 (Intern A): raw JDBC CRUD for products. Needs the shop database from sql/ and the driver jar:
//   java -cp postgresql-42.7.4.jar Lab3JdbcCrud.java
// Check (main below, on a fresh shop database):
//   7 products
//   found: Product[id=1, name=Laptop, category=Electronics, price=1200.00]
//   missing: empty
//   after insert: 8 products
//   after price change: 1250.00
//   after delete: 7 products
public class Lab3JdbcCrud {
    record Product(int id, String name, String category, BigDecimal price) {}

    static final String URL = "jdbc:postgresql://localhost:5432/shop";

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, "postgres", "postgres")) {
            System.out.println(findAll(conn).size() + " products");
            System.out.println("found: " + findById(conn, 1).orElse(null));
            System.out.println("missing: " + findById(conn, 999).map(Product::toString).orElse("empty"));

            int newId = insert(conn, "Webcam", "Accessories", new BigDecimal("55.00"));
            System.out.println("after insert: " + findAll(conn).size() + " products");

            updatePrice(conn, 1, new BigDecimal("1250.00"));
            System.out.println("after price change: " + findById(conn, 1).orElseThrow().price());
            updatePrice(conn, 1, new BigDecimal("1200.00"));        // put it back

            delete(conn, newId);
            System.out.println("after delete: " + findAll(conn).size() + " products");
        }
    }

    static List<Product> findAll(Connection conn) throws SQLException {
        List<Product> products = new ArrayList<>();
        // TODO: SELECT id, name, category, price FROM products ORDER BY id; map each row to a Product
        return products;
    }

    static Optional<Product> findById(Connection conn, int id) throws SQLException {
        // TODO: SELECT ... WHERE id = ?   (PreparedStatement, setInt)
        return Optional.empty();
    }

    // Returns the generated id. Hint: conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), then getGeneratedKeys()
    static int insert(Connection conn, String name, String category, BigDecimal price) throws SQLException {
        // TODO
        return -1;
    }

    static void updatePrice(Connection conn, int id, BigDecimal price) throws SQLException {
        // TODO: UPDATE products SET price = ? WHERE id = ?
    }

    static void delete(Connection conn, int id) throws SQLException {
        // TODO: DELETE FROM products WHERE id = ?
    }
}
