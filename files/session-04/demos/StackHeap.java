// Variables hold primitives or REFERENCES. new creates objects on the heap.
public class StackHeap {
    public static void main(String[] args) {
        int a = 5;
        int b = a;
        b = 99;
        System.out.println(a);                  // 5: primitives are copied

        Box x = new Box(10);
        Box y = x;                              // copies the reference, not the object
        y.size = 99;
        System.out.println(x.size);             // 99: same object

        Box p = new Box(7);
        Box q = new Box(7);
        System.out.println(p == q);             // false: different objects
        System.out.println(p.equals(q));        // false until we write equals

        Box none = null;
        System.out.println(none.size);          // NullPointerException (on purpose)
    }
}

class Box {
    int size;

    Box(int size) {
        this.size = size;
    }
}
