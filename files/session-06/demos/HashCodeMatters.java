import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// HashSet and HashMap use hashCode() then equals(). Forget hashCode and duplicates sneak in.
public class HashCodeMatters {
    public static void main(String[] args) {
        Set<NoHash> bad = new HashSet<>();
        bad.add(new NoHash("978-1"));
        bad.add(new NoHash("978-1"));
        System.out.println(bad.size());             // 2 (usually): equals is never even called

        Set<Isbn> good = new HashSet<>();
        good.add(new Isbn("978-1"));
        good.add(new Isbn("978-1"));
        System.out.println(good.size());            // 1
        System.out.println(good.contains(new Isbn("978-1")));  // true
    }
}

class NoHash {
    final String code;

    NoHash(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NoHash other && code.equals(other.code);
    }
    // no hashCode(): each object gets a different one from Object
}

class Isbn {
    final String code;

    Isbn(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Isbn other && code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
