package com.flairs.iocdemo;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// A CommandLineRunner runs once, right after the context is ready. We use it to look inside the container.
@Component
public class DemoRunner implements CommandLineRunner {

    private final ApplicationContext context;
    private final OrderService orderService;
    private final Environment env;

    public DemoRunner(ApplicationContext context, OrderService orderService, Environment env) {
        this.context = context;
        this.orderService = orderService;
        this.env = env;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== 1. Active profile: " + Arrays.toString(env.getActiveProfiles()));

        System.out.println("\n=== 2. Beans from our package (the container's contents)");
        Arrays.stream(context.getBeanDefinitionNames())
                .filter(name -> context.getBean(name).getClass().getName().startsWith("com.flairs"))
                .sorted()
                .forEach(name -> System.out.println("  " + name + " -> " + context.getBean(name).getClass().getSimpleName()));

        System.out.println("\n=== 3. Using the service (every dependency was injected)");
        orderService.placeOrder("ali@example.com", 100.0);

        System.out.println("\n=== 4. Singleton scope: asking twice gives the SAME object");
        OrderService a = context.getBean(OrderService.class);
        OrderService b = context.getBean(OrderService.class);
        System.out.println("  a == b ? " + (a == b));

        System.out.println("\n=== 5. Total beans in the container: " + context.getBeanDefinitionCount()
                + " (most come from Spring Boot auto-configuration)\n");
    }
}
