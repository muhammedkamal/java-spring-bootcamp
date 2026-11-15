package com.flairs.taskapi.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableMethodSecurity          // turns on @PreAuthorize on controller and service methods
public class SecurityConfig {

    private static final String[] PUBLIC = {
        "/api/auth/register", "/api/auth/login", "/api/auth/refresh",
        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error",
        // Session 14: Docker, the gateway and load balancers ask "are you alive / ready?" without a token
        "/actuator/health", "/actuator/health/**", "/actuator/info"
    };

    @Bean
    SecurityFilterChain api(HttpSecurity http,
                            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
        http
            // A REST API with tokens in the Authorization header: no cookies, so no CSRF risk and no session.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> { })                      // uses the CorsConfigurationSource bean below
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()      // CORS pre-flight
                .requestMatchers("/api/admin/**").hasRole("ADMIN")           // role check by URL
                .requestMatchers("/actuator/**").hasRole("ADMIN")            // metrics: admins only
                .anyRequest().authenticated())
            // Read "Authorization: Bearer <jwt>", verify it with the JwtDecoder bean, build the Authentication
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                // 401 and 403 go through GlobalExceptionHandler, so they are ProblemDetail JSON like every other error
                .authenticationEntryPoint((req, res, ex) -> resolver.resolveException(req, res, null, ex))
                .accessDeniedHandler((req, res, ex) -> resolver.resolveException(req, res, null, ex)))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> resolver.resolveException(req, res, null, ex))
                .accessDeniedHandler((req, res, ex) -> resolver.resolveException(req, res, null, ex)));
        return http.build();
    }

    // The "roles" claim ["ADMIN"] becomes the authority ROLE_ADMIN, which hasRole("ADMIN") checks.
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    // BCrypt: slow on purpose and salted, so stolen hashes are hard to crack.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(CorsProperties props) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(props.allowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
