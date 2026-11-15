package com.flairs.taskapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flairs.taskapi.events.RabbitConfig;
import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Session 14: does creating and completing a task really put a message on RabbitMQ?
// The test plays the part of a consumer: it binds its own temporary queue to the exchange and reads from it.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TaskEventsIT {

    @Autowired MockMvc mvc;
    @Autowired AmqpAdmin admin;
    @Autowired RabbitTemplate rabbit;

    // A queue just for this test, removed in cleanUp(). Durable because RabbitMQ 4 no longer allows
    // transient queues that several connections can use
    Queue queue = new Queue("test.task-events", true, false, false);
    Long taskId;
    String token;

    @BeforeEach
    void bindATestQueue() throws Exception {
        admin.declareQueue(queue);
        admin.purgeQueue(queue.getName(), false);   // start empty
        admin.declareBinding(BindingBuilder.bind(queue).to(new TopicExchange(RabbitConfig.EXCHANGE)).with("task.#"));
        token = TestTokens.login(mvc, "sara@flairs.dev");
    }

    @AfterEach
    void cleanUp() throws Exception {
        if (taskId != null) {
            mvc.perform(delete("/api/tasks/" + taskId).header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
        }
        admin.deleteQueue(queue.getName());
    }

    @Test
    void createAndCompleteSendTwoEvents() throws Exception {
        String created = mvc.perform(post("/api/tasks").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Event test\",\"priority\":\"LOW\",\"projectId\":1}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        taskId = ((Number) JsonPath.read(created, "$.id")).longValue();

        mvc.perform(patch("/api/tasks/" + taskId + "/complete").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        String first = receive();
        assertThat((String) JsonPath.read(first, "$.type")).isEqualTo("CREATED");
        assertThat(((Number) JsonPath.read(first, "$.taskId")).longValue()).isEqualTo(taskId);
        assertThat(((Number) JsonPath.read(first, "$.ownerId")).longValue()).isEqualTo(2L);

        String second = receive();
        assertThat((String) JsonPath.read(second, "$.type")).isEqualTo("COMPLETED");
    }

    @Test
    void aRejectedRequestSendsNothing() throws Exception {
        mvc.perform(post("/api/tasks").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Not mine\",\"priority\":\"LOW\",\"projectId\":3}"))   // Omar's project
                .andExpect(status().isNotFound());

        assertThat(rabbit.receive(queue.getName(), 1000)).isNull();
    }

    // Raw JSON body: a consumer in any language would see exactly this
    private String receive() {
        Message message = rabbit.receive(queue.getName(), 5000);
        assertThat(message).as("an event within 5 seconds").isNotNull();
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }
}
