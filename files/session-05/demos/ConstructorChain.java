// Predict the output: constructors run from the top of the family down.
public class ConstructorChain {
    public static void main(String[] args) {
        new C();
        System.out.println("---");
        new B();
    }
}

class A {
    A() {
        System.out.println("A");
    }
}

class B extends A {
    B() {
        // super(); is inserted here by the compiler
        System.out.println("B");
    }
}

class C extends B {
    C() {
        System.out.println("C");
    }
}
// Output:
// A
// B
// C
// ---
// A
// B
