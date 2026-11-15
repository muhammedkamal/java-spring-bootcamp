package com.flairs.taskapi;

import org.springframework.boot.SpringApplication;

// Run the app against a throw-away Testcontainers database, no compose.yaml needed:
//   mvn spring-boot:test-run      or run this main() from the IDE
public class TestTaskApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(TaskApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
