
// enum Department { ENGINEERING, SALES, SUPPORT }
// abstract class Employee(name, department): abstract double monthlyPay();
//   toString() -> String.format("%-5s %-12s %10.2f", name, department, monthlyPay())
// SalariedEmployee(name, dept, salary): pays the salary.
// HourlyEmployee(name, dept, rate, hours): up to 160 hours at rate, extra hours at 1.5 x rate.
// Manager(name, dept, salary, bonus) extends SalariedEmployee: salary + bonus (use super.monthlyPay()).
// Check (main below):
//   Mona  ENGINEERING    18000.00
//   Ali   SUPPORT        17500.00
//   Sara  ENGINEERING    30000.00
//   Hany  SALES           9600.00
//   Total payroll: 75100.00
//   Engineering: 48000.00
public class Lab3Payroll {
    public static void main(String[] args) {
        Employee[] staff = {
                new SalariedEmployee("Mona", Department.ENGINEERING, 18000),
                new HourlyEmployee("Ali", Department.SUPPORT, 100, 170),
                new Manager("Sara", Department.ENGINEERING, 25000, 5000),
                new HourlyEmployee("Hany", Department.SALES, 120, 80)
        };

        double total = 0;
        for (Employee e : staff) {
            System.out.println(e);
            total += e.monthlyPay();
        }
        System.out.printf("Total payroll: %.2f%n", total);
        System.out.printf("Engineering: %.2f%n", totalFor(staff, Department.ENGINEERING));
    }

    static double totalFor(Employee[] staff, Department dept) {
        double sum = 0;
        for (Employee e : staff) {
            if (e.getDepartment() == dept) {
                sum += e.monthlyPay();
            }
        }
        return sum;
    }
}

enum Department {
    ENGINEERING, SALES, SUPPORT
}

abstract class Employee {
    private final String name;
    private final Department department;

    Employee(String name, Department department) {
        this.name = name;
        this.department = department;
    }

    String getName() {
        return name;
    }

    Department getDepartment() {
        return department;
    }

    abstract double monthlyPay();

    @Override
    public String toString() {
        return String.format("%-5s %-12s %10.2f", name, department, monthlyPay());
    }
}

class SalariedEmployee extends Employee {
    private final double salary;

    SalariedEmployee(String name, Department department, double salary) {
        super(name, department);
        this.salary = salary;
    }

    @Override
    double monthlyPay() {
        return salary;
    }
}

class HourlyEmployee extends Employee {
    static final int NORMAL_HOURS = 160;
    private final double rate;
    private final int hours;

    HourlyEmployee(String name, Department department, double rate, int hours) {
        super(name, department);
        this.rate = rate;
        this.hours = hours;
    }

    @Override
    double monthlyPay() {
        if (hours <= NORMAL_HOURS) {
            return rate * hours;
        }
        int overtime = hours - NORMAL_HOURS;
        return rate * NORMAL_HOURS + rate * 1.5 * overtime;
    }
}

class Manager extends SalariedEmployee {
    private final double bonus;

    Manager(String name, Department department, double salary, double bonus) {
        super(name, department, salary);
        this.bonus = bonus;
    }

    @Override
    double monthlyPay() {
        return super.monthlyPay() + bonus;
    }
}
