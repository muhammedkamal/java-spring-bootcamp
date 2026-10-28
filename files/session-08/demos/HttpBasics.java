import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

// A tiny HTTP server and client in one file, so you can see methods, status codes and headers
// without any framework. In Week 5 Spring Boot will write the server part for you.
public class HttpBasics {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8089), 0);

        server.createContext("/books", exchange -> {
            String method = exchange.getRequestMethod();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            int status;
            String json;
            switch (method) {
                case "GET" -> { status = 200; json = "[{\"id\":1,\"title\":\"Dune\"},{\"id\":2,\"title\":\"Emma\"}]"; }
                case "POST" -> { status = 201; json = "{\"id\":3,\"created\":" + body + "}"; }
                case "DELETE" -> { status = 204; json = ""; }
                default -> { status = 405; json = "{\"error\":\"Method not allowed\"}"; }
            }
            send(exchange, status, json);
        });
        server.createContext("/secret", exchange -> send(exchange, 401, "{\"error\":\"Login required\"}"));
        server.start();

        HttpClient client = HttpClient.newHttpClient();
        show(client, HttpRequest.newBuilder(URI.create("http://localhost:8089/books")).GET().build());
        // GET /books -> 200 [{"id":1,"title":"Dune"},{"id":2,"title":"Emma"}]

        show(client, HttpRequest.newBuilder(URI.create("http://localhost:8089/books"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"Persuasion\"}")).build());
        // POST /books -> 201 {"id":3,"created":{"title":"Persuasion"}}

        show(client, HttpRequest.newBuilder(URI.create("http://localhost:8089/books/1")).DELETE().build());
        // DELETE /books/1 -> 204 (no body: "done, nothing to send back")

        show(client, HttpRequest.newBuilder(URI.create("http://localhost:8089/books")).PUT(HttpRequest.BodyPublishers.noBody()).build());
        // PUT /books -> 405 {"error":"Method not allowed"}

        show(client, HttpRequest.newBuilder(URI.create("http://localhost:8089/secret")).GET().build());
        // GET /secret -> 401 {"error":"Login required"}

        server.stop(0);
    }

    static void send(com.sun.net.httpserver.HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, status == 204 ? -1 : bytes.length);
        if (status != 204) exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    static void show(HttpClient client, HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(request.method() + " " + request.uri().getPath() + " -> "
                + response.statusCode() + " " + response.body().strip());
    }
}
