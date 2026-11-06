import java.util.ArrayList;
import java.util.List;

// Lab 1 solution. Run: java Lab1DiRefactor.java
public class Lab1DiRefactor {

    interface Logger {
        void log(String message);
    }

    static class ConsoleLogger implements Logger {
        public void log(String message) {
            System.out.println(message);
        }
    }

    static class PrefixLogger implements Logger {
        private final String prefix;

        PrefixLogger(String prefix) {
            this.prefix = prefix;
        }

        public void log(String message) {
            System.out.println(prefix + " " + message);
        }
    }

    static class ReportService {
        private final Logger logger;

        ReportService(Logger logger) {
            this.logger = logger;
        }

        void run(String name) {
            logger.log("Report: " + name);
        }
    }

    public static void main(String[] args) {
        new ReportService(new ConsoleLogger()).run("sales");
        new ReportService(new PrefixLogger("[AUDIT]")).run("sales");

        List<String> captured = new ArrayList<>();
        new ReportService(captured::add).run("sales");      // Logger has one method, so a method reference works
        System.out.println("captured messages: " + captured.size());
    }
}
