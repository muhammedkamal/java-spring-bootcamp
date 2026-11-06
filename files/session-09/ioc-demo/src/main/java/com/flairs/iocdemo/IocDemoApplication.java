package com.flairs.iocdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
// Component scan starts in THIS package, so every bean must live in com.flairs.iocdemo or below.
@SpringBootApplication
@ConfigurationPropertiesScan          // finds records/classes annotated with @ConfigurationProperties
public class IocDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IocDemoApplication.class, args);
    }
}
