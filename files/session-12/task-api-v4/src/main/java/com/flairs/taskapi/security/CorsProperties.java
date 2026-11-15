package com.flairs.taskapi.security;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

// The browser front ends allowed to call this API, for example http://localhost:5173 (Vite dev server)
@ConfigurationProperties("app.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
