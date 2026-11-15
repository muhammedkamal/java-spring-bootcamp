package com.flairs.taskapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI taskApiInfo() {
        return new OpenAPI().info(new Info()
                .title("Task API")
                .version("v3")
                .description("Session 11: the Task API on PostgreSQL with JPA and Flyway"));
    }
}
