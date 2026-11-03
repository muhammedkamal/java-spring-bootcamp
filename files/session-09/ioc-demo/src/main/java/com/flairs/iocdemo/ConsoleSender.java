package com.flairs.iocdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// Only exists when the "dev" profile is active
@Component
@Profile("dev")
public class ConsoleSender implements NotificationSender {

    @Override
    public void send(String to, String message) {
        System.out.println("[CONSOLE -> " + to + "] " + message);
    }
}
