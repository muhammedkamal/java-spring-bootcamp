// Lab 3: Payroll.
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

        // TODO: print each employee and the total payroll
        double totalPayroll = 0;
        for (Employee employee : staff){
            System.out.println(employee);
            totalPayroll += employee.monthlyPay();
        }
        System.out.println("Total payroll: " + totalPayroll);
        // TODO: print the total for ENGINEERING using totalFor
        System.out.println("Engineering: " + totalFor(staff,Department.ENGINEERING));
    }

    static double totalFor(Employee[] staff, Department dept) {
        // TODO
        double totalfor = 0;
        for (Employee employee : staff){
            if (employee.getDepartment() == dept){
                   totalfor += employee.monthlyPay();
            }
        }
        return totalfor;
    }
}

// TODO: enum Department
enum Department {
    ENGINEERING, SALES, SUPPORT
}
// TODO: abstract class Employee
abstract class Employee{
    private Department department;
    private String name;
    Employee(String name, Department department){
        this.department = department;
        this.name = name;
    }
    abstract double monthlyPay();
    Department getDepartment(){return department;}
    @Override
    public String toString() {
        return String.format("%-5s %-12s %10.2f", name, getDepartment(), monthlyPay());
    }
}
// TODO: class SalariedEmployee extends Employee
class SalariedEmployee extends Employee {
    private double salary;
    SalariedEmployee(String name ,Department dept, double salary){
        super(name,dept);
        this.salary = salary;
    }
    @Override
    double monthlyPay() {
        return salary;
    }
}
// TODO: class HourlyEmployee extends Employee
class HourlyEmployee extends Employee{
    private double rate;
    private double hours;
    HourlyEmployee(String name ,Department dept, double rate, double hours){
        super(name,dept);
        this.rate = rate;
        this.hours = hours;
    }

    @Override
    double monthlyPay() {
        double extraHours = hours - 160;
        if (extraHours > 0){
            return ((1.5 * rate) * extraHours) + (rate * (hours - extraHours)) ;
        }
        return rate * hours;
    }
}
// TODO: class Manager extends SalariedEmployee
class Manager extends SalariedEmployee {
    private double Bouns;
    Manager(String name ,Department dept, double salary, double Bouns){
        super(name,dept,salary);
        this.Bouns = Bouns;
    }

    @Override
    double monthlyPay() {
        return super.monthlyPay() + Bouns;
    }
}
