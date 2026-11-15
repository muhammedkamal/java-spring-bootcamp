package com.flairs.notifications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.testcontainers.rabbitmq.RabbitMQContainer;

// The real path: a JSON message on the tasks.events exchange (exactly what the Task API sends)
// -> our queue -> the listener -> the store. And a broken message ends up in the dead letter queue.
@SpringBootTest
@Import(NotificationFlowIT.Containers.class)
class NotificationFlowIT {

    @TestConfiguration(proxyBeanMethods = false)
    static class Containers {
        @Bean
        @ServiceConnection
        RabbitMQContainer rabbit() {
            return new RabbitMQContainer("rabbitmq:4-management-alpine");
        }
    }

    @Autowired RabbitTemplate rabbit;
    @Autowired NotificationStore store;

    @Test
    void aTaskEventBecomesANotification() {
        send("task.created", """
                {"type":"CREATED","taskId":41,"title":"Write the Dockerfile","ownerId":7,"occurredAt":"2026-01-01T09:00:00Z"}""");

        // the listener runs on another thread, a moment later: wait for it instead of Thread.sleep
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(store.forOwner(7L)).extracting(Notification::message).contains("New task: Write the Dockerfile"));
    }

    @Test
    void aBrokenMessageGoesToTheDeadLetterQueue() {
        send("task.created", "{\"type\":\"CREATED\",\"title\":\"no owner\"}");

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                assertThat(rabbit.receive(RabbitConfig.DEAD_LETTER_QUEUE)).isNotNull());
    }

    private void send(String routingKey, String json) {
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
        rabbit.send(RabbitConfig.EXCHANGE, routingKey, message);
    }
}
