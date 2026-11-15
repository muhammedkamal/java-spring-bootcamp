package com.flairs.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Session 14: a second, small service. It never calls the Task API and the Task API never calls it:
// they only share a RabbitMQ exchange (tasks.events) and the JWT secret.
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
