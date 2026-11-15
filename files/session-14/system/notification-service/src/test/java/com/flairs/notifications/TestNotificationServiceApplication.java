package com.flairs.notifications;

import org.springframework.boot.SpringApplication;

// Run THIS from the IDE to start the service with a RabbitMQ in Docker, without compose.yaml
public class TestNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(NotificationServiceApplication::main).with(NotificationFlowIT.Containers.class).run(args);
    }
}
