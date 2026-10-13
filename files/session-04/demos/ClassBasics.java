// A class is a blueprint. An object is one thing built from it.
public class ClassBasics {
    public static void main(String[] args) {
        Dog rex = new Dog("Rex", 3);
        Dog luna = new Dog("Luna", 5);

        rex.bark();
        luna.bark();
        System.out.println(rex.name + " is " + rex.age);

        rex.birthday();
        System.out.println(rex.name + " is now " + rex.age);
    }
}

class Dog {
    String name;
    int age;

    Dog(String name, int age) {      // constructor: same name as the class
        this.name = name;            // this.name is the field, name is the parameter
        this.age = age;
    }

    void bark() {
        System.out.println(name + " says woof");
    }

    void birthday() {
        age++;
    }
}
