package com.flairs.taskapi;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

// One real PostgreSQL 17 in Docker for the tests. @ServiceConnection gives Spring the URL, user and password,
// so no datasource settings are needed. Spring starts the container once and reuses it for every test class
// that imports this configuration (same Spring context = same container).
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return new PostgreSQLContainer("postgres:17-alpine");
    }
}
