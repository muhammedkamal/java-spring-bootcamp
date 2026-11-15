package com.flairs.taskapi;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;

// Real PostgreSQL, Redis and RabbitMQ in Docker for the tests. @ServiceConnection gives Spring the host,
// port, user and password of each one, so no connection settings are needed. Spring starts the containers
// once and reuses them for every test class that imports this configuration (same context = same containers).
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return new PostgreSQLContainer("postgres:17-alpine");
    }

    // Session 14: Redis has no dedicated Testcontainers class; a GenericContainer plus name = "redis"
    // tells Spring Boot what it is
    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redis() {
        return new GenericContainer<>("redis:8-alpine").withExposedPorts(6379);
    }

    // Session 14
    @Bean
    @ServiceConnection
    RabbitMQContainer rabbit() {
        return new RabbitMQContainer("rabbitmq:4-management-alpine");
    }
}
