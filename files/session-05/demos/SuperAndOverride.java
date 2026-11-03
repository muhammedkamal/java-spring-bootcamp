// Overriding: a subclass replaces a parent method. super.method() still reaches the parent version.
public class SuperAndOverride {
    public static void main(String[] args) {
        Employee e = new Employee("Mona", 10000);
        Manager m = new Manager("Omar", 15000, 3000);

        System.out.println(e.describe());       // Mona earns 10000.0
        System.out.println(m.describe());       // Omar earns 18000.0 (bonus 3000.0)
        System.out.println(m);                  // Manager[Omar]
    }
}

class Employee {
    protected String name;
    protected double salary;

    Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    double pay() {
        return salary;
    }

    String describe() {
        return name + " earns " + pay();
    }
}

class Manager extends Employee {
    private double bonus;

    Manager(String name, double salary, double bonus) {
        super(name, salary);
        this.bonus = bonus;
    }

    @Override                                   // the compiler checks the signature matches
    double pay() {
        return super.pay() + bonus;             // reuse the parent, then add
    }

    @Override
    String describe() {
        return super.describe() + " (bonus " + bonus + ")";
    }

    @Override
    public String toString() {                  // every class extends Object
        return "Manager[" + name + "]";
    }
}
