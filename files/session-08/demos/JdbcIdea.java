import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// How Java talks to a database with plain JDBC. This is the work Spring Data JPA does for you in Week 6.
// Needs PostgreSQL running (docker compose up -d) and the driver jar on the classpath:
//   java -cp postgresql-42.7.4.jar JdbcIdea.java
public class JdbcIdea {
    static final String URL = "jdbc:postgresql://localhost:5432/shop";

    public static void main(String[] args) {
        // try-with-resources closes the connection, statement and result set, even on errors
        try (Connection conn = DriverManager.getConnection(URL, "postgres", "postgres")) {

            // 1. SELECT with a parameter. Never build SQL by joining strings (SQL injection).
            String sql = "SELECT name, price FROM products WHERE category = ? ORDER BY price DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "Electronics");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getString("name") + " " + rs.getBigDecimal("price"));
                    }
                }
            }
            // Laptop 1200.00
            // Phone 700.00
            // Monitor 250.00

            // 2. A transaction: both changes happen, or neither
            conn.setAutoCommit(false);
            try (PreparedStatement update = conn.prepareStatement("UPDATE products SET price = price + 10 WHERE name = ?");
                 PreparedStatement insert = conn.prepareStatement("INSERT INTO customers (name, city) VALUES (?, ?)")) {
                update.setString(1, "Mouse");
                update.executeUpdate();
                insert.setString(1, "Test");
                insert.setString(2, "Cairo");
                insert.executeUpdate();
                conn.rollback();                        // change to commit() to keep the changes
                System.out.println("rolled back");      // rolled back
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        // Notice the cost: connection handling, manual mapping row -> object, SQL strings in Java.
        // That repetition is why ORMs (JPA/Hibernate) exist.
    }
}
