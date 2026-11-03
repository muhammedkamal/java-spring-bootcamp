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
        // TODO: print the total for ENGINEERING using totalFor
    }

    static double totalFor(Employee[] staff, Department dept) {
        // TODO
        return 0;
    }
}

// TODO: enum Department
// TODO: abstract class Employee
// TODO: class SalariedEmployee extends Employee
// TODO: class HourlyEmployee extends Employee
// TODO: class Manager extends SalariedEmployee
