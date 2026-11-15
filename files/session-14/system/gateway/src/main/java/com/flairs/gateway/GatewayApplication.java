package com.flairs.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// No controllers: the routes in application.yml are the whole program.
// The gateway does NOT check tokens. It passes the Authorization header on, and each service checks it.
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
