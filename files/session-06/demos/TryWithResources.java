// try-with-resources: anything AutoCloseable is closed for you, even when an exception happens.
public class TryWithResources {
    public static void main(String[] args) {
        try (Connection a = new Connection("db"); Connection b = new Connection("cache")) {
            a.query("SELECT 1");
            b.query("BOOM");                        // throws
            System.out.println("never printed");
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage());
        }
        // open db, open cache, db runs SELECT 1, close cache, close db, Caught: bad query
        // Resources close in reverse order, BEFORE the catch block runs.
    }
}

class Connection implements AutoCloseable {
    private final String name;

    Connection(String name) {
        this.name = name;
        System.out.println("open " + name);
    }

    void query(String sql) {
        if (sql.equals("BOOM")) {
            throw new IllegalStateException("bad query");
        }
        System.out.println(name + " runs " + sql);
    }

    @Override
    public void close() {
        System.out.println("close " + name);
    }
}
