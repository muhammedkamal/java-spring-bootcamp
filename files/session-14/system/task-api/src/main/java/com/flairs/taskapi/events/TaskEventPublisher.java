package com.flairs.taskapi.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

// TaskService publishes a Spring event inside its transaction. This listener runs only AFTER the
// transaction COMMITS, so we never announce a task that was rolled back.
@Component
public class TaskEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TaskEventPublisher.class);

    private final RabbitTemplate rabbit;

    public TaskEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @TransactionalEventListener      // phase = AFTER_COMMIT
    public void send(TaskEvent event) {
        try {
            rabbit.convertAndSend(RabbitConfig.EXCHANGE, event.routingKey(), event);
            log.debug("Sent {} for task {}", event.routingKey(), event.taskId());
        } catch (AmqpException e) {
            // The task is already saved: do not turn a broker problem into a failed HTTP request.
            // The event is lost. If every event must arrive, use the "transactional outbox" pattern.
            log.error("Could not send {} for task {}: {}", event.routingKey(), event.taskId(), e.getMessage());
        }
    }
}
