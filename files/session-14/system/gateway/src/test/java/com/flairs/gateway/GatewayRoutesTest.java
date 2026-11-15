package com.flairs.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

// Two tiny fake services (the JDK's built-in HTTP server) stand in for the Task API and the notification service.
// Each answers with its own name and echoes the Authorization header it received.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRoutesTest {

    static HttpServer taskApi = fakeService("task-api");
    static HttpServer notifications = fakeService("notification-service");

    @DynamicPropertySource
    static void backends(DynamicPropertyRegistry registry) {
        registry.add("TASK_API_URL", () -> "http://localhost:" + taskApi.getAddress().getPort());
        registry.add("NOTIFICATIONS_URL", () -> "http://localhost:" + notifications.getAddress().getPort());
    }

    @AfterAll
    static void stop() {
        taskApi.stop(0);
        notifications.stop(0);
    }

    @Value("${local.server.port}") int port;
    HttpClient http = HttpClient.newHttpClient();

    @Test
    void tasksGoToTheTaskApi() throws Exception {
        HttpResponse<String> response = get("/api/tasks?page=0");

        assertThat(response.body()).isEqualTo("task-api /api/tasks?page=0 auth=Bearer abc");
        assertThat(response.headers().firstValue("X-Served-By")).hasValue("task-api");
    }

    @Test
    void notificationsGoToTheNotificationService() throws Exception {
        HttpResponse<String> response = get("/api/notifications");

        assertThat(response.body()).startsWith("notification-service /api/notifications");
        assertThat(response.headers().firstValue("X-Served-By")).hasValue("notification-service");
    }

    @Test
    void anUnknownPathIs404() throws Exception {
        assertThat(get("/admin/secret").statusCode()).isEqualTo(404);
    }

    @Test
    void theGatewayHasItsOwnHealthCheck() throws Exception {
        assertThat(get("/actuator/health").body()).contains("\"status\":\"UP\"");
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Authorization", "Bearer abc").build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpServer fakeService(String name) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/", exchange -> {
                String body = name + " " + exchange.getRequestURI() + " auth=" + exchange.getRequestHeaders().getFirst("Authorization");
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
            });
            server.start();
            return server;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
