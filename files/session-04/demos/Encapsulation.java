// Hide the data, expose safe methods. The object protects its own rules.
public class Encapsulation {
    public static void main(String[] args) {
        Thermostat t = new Thermostat();
        t.setTarget(21);
        System.out.println(t.getTarget());      // 21
        t.setTarget(99);                        // rejected
        System.out.println(t.getTarget());      // still 21
        // t.target = 99;                       // does not compile: private
    }
}

class Thermostat {
    private int target = 20;

    public int getTarget() {
        return target;
    }

    public void setTarget(int value) {
        if (value < 10 || value > 30) {
            System.out.println("Rejected: " + value);
            return;
        }
        target = value;
    }
}
