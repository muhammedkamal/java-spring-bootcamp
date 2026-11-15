package com.flairs.libraryapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flairs.libraryapi.config.CacheConfig;
import com.flairs.libraryapi.events.RabbitConfig;
import com.jayway.jsonpath.JsonPath;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Session 14 lab: health, the books cache and the loan events, on real PostgreSQL, Redis and RabbitMQ.
// These tests are finished: remove each @Disabled when its TODO in the main code is done.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ShipItIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectProvider<CacheManager> caches;   // a provider: the starter has no CacheManager until TODO 2
    @Autowired RabbitAdmin admin;
    @Autowired RabbitTemplate rabbit;

    // Durable: RabbitMQ 4 no longer allows transient queues that several connections share
    final Queue queue = new Queue("test.library-events", true, false, false);

    @BeforeEach
    void listen() {
        admin.declareQueue(queue);
        admin.purgeQueue(queue.getName(), false);
        admin.declareBinding(BindingBuilder.bind(queue).to(new TopicExchange(RabbitConfig.EXCHANGE)).with("loan.#"));
    }

    @AfterEach
    void stopListening() {
        admin.deleteQueue(queue.getName());
    }

    // TODO 1
    @Test
    @Disabled("TODO 1")
    void healthIsPublicAndUp() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
    }

    // TODO 2
    @Test
    @Disabled("TODO 2")
    void aBookIsCachedAndAnUpdateEvictsIt() throws Exception {
        Cache books = caches.getObject().getCache(CacheConfig.BOOKS);
        books.clear();
        String sara = login("sara@example.com");
        String librarian = login("librarian@example.com");

        mvc.perform(get("/api/books/6").header("Authorization", "Bearer " + sara)).andExpect(status().isOk());
        assertThat(books.get(6L)).isNotNull();

        mvc.perform(put("/api/books/6").header("Authorization", "Bearer " + librarian)
                        .contentType(MediaType.APPLICATION_JSON).content(palaceWalk(2)))
                .andExpect(status().isOk());
        // The delete in Redis can land a moment after the response
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(books.get(6L)).isNull());
        mvc.perform(get("/api/books/6").header("Authorization", "Bearer " + sara))
                .andExpect(jsonPath("$.copies").value(2));

        mvc.perform(put("/api/books/6").header("Authorization", "Bearer " + librarian)
                .contentType(MediaType.APPLICATION_JSON).content(palaceWalk(1)));   // as we found it
    }

    // TODO 4
    @Test
    @Disabled("TODO 4")
    void borrowAndReturnSendTwoEvents() throws Exception {
        String omar = login("omar@example.com");
        String body = mvc.perform(post("/api/loans").header("Authorization", "Bearer " + omar)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"bookId\": 5}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer loanId = JsonPath.read(body, "$.id");
        mvc.perform(patch("/api/loans/" + loanId + "/return").header("Authorization", "Bearer " + omar))
                .andExpect(status().isOk());

        String borrowed = next();
        assertThat((String) JsonPath.read(borrowed, "$.type")).isEqualTo("BORROWED");
        assertThat((Integer) JsonPath.read(borrowed, "$.loanId")).isEqualTo(loanId);
        assertThat((Integer) JsonPath.read(borrowed, "$.memberId")).isEqualTo(2);
        assertThat((String) JsonPath.read(next(), "$.type")).isEqualTo("RETURNED");
    }

    // TODO 4
    @Test
    @Disabled("TODO 4")
    void aRefusedBorrowSendsNothing() throws Exception {
        // Animal Farm's only copy is out (seed data)
        mvc.perform(post("/api/loans").header("Authorization", "Bearer " + login("omar@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"bookId\": 2}"))
                .andExpect(status().isConflict());
        assertThat(rabbit.receive(queue.getName(), 1000)).isNull();
    }

    private String next() {
        Message message = rabbit.receive(queue.getName(), 5000);
        assertThat(message).as("a message within 5 seconds").isNotNull();
        return new String(message.getBody());
    }

    private static String palaceWalk(int copies) {
        return "{\"title\":\"Palace Walk\",\"isbn\":\"9780385264662\",\"year\":1956,\"copies\":" + copies
                + ",\"authorId\":4}";
    }

    private String login(String email) throws Exception {
        String json = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.accessToken");
    }
}
