package com.flairs.taskapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Adds an "Authorize" button to Swagger UI: paste the accessToken from /api/auth/login (without "Bearer ").
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI taskApiInfo() {
        return new OpenAPI()
                .info(new Info().title("Task API").version("v4")
                        .description("Session 12: the Task API secured with Spring Security and JWT"))
                .components(new Components().addSecuritySchemes("bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
