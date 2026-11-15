package com.flairs.taskapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan        // finds JwtProperties and CorsProperties
public class TaskApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApiApplication.class, args);
    }
}
