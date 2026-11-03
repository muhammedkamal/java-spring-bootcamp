// toString, equals and hashCode: how an object describes and compares itself.
import java.util.Objects;

public class ObjectMethods {
    public static void main(String[] args) {
        Point p = new Point(2, 3);
        Point q = new Point(2, 3);

        System.out.println(p);                          // Point(2, 3)
        System.out.println(p == q);                     // false
        System.out.println(p.equals(q));                // true
        System.out.println(p.hashCode() == q.hashCode()); // true
    }
}

class Point {
    private final int x;
    private final int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point other)) return false;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
