package com.flairs.iocdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// Only exists when the "prod" profile is active (a real one would call an SMTP server)
@Component
@Profile("prod")
public class EmailSender implements NotificationSender {

    @Override
    public void send(String to, String message) {
        System.out.println("[EMAIL  -> " + to + "] " + message);
    }
}
