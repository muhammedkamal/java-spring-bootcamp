import java.util.ArrayList;
import java.util.List;

// Lab 1: refactor to constructor injection. Run: java Lab1DiRefactor.java
//
// ReportService is tightly coupled to ConsoleLogger.
// 1. Introduce an interface `Logger` with one method: void log(String message).
// 2. Make ConsoleLogger implement it. Add a second class PrefixLogger(String prefix) that prints "<prefix> message".
// 3. Change ReportService so it receives a Logger through its CONSTRUCTOR (private final field, no `new` inside).
// 4. In main(), wire three services by hand: one with ConsoleLogger, one with PrefixLogger("[AUDIT]"),
//    one with a lambda that stores messages in a List, and print that list's size at the end.
//
// Expected output after the refactor:
//   Report: sales
//   [AUDIT] Report: sales
//   captured messages: 1
public class Lab1DiRefactor {

    public interface Logger {
        void log(String message);
    }

    static class ConsoleLogger implements Logger {
        public void log(String message) {
            System.out.println(message);
        }

    }
    static class PrefixLogger implements Logger{
        private final String prefix;

        PrefixLogger(String prefix){
            this.prefix = prefix;
        }
        public void log(String message) {
            System.out.println(prefix+" "+ message);
        }
    }
    static class ReportService {
        private final Logger logger;

        ReportService(Logger logger){
            this.logger = logger;
        }
        void run(String name) {
            logger.log("Report: " + name);
        }
    }

    public static void main(String[] args) {
        // TODO: wire the three versions by hand as described above
        new ReportService(new ConsoleLogger()).run("sales");
        new ReportService(new PrefixLogger("[AUDIT]")).run("sales");
        List<String> captured = new ArrayList<>();
        new ReportService(message -> captured.add(message)).run("sales");      // Logger has one method, so a method reference works
        System.out.println("captured messages: " + captured.size());
    }
}
