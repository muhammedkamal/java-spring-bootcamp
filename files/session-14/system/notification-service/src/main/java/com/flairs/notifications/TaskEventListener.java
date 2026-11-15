package com.flairs.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {

    private static final Logger log = LoggerFactory.getLogger(TaskEventListener.class);

    private final NotificationStore store;

    public TaskEventListener(NotificationStore store) {
        this.store = store;
    }

    // Returns normally = ACK: RabbitMQ deletes the message.
    // Throws = the message is retried, EXCEPT AmqpRejectAndDontRequeueException: straight to the dead letter queue.
    // A broken message will never get better, so retrying it forever would only block the queue.
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void on(TaskEvent event) {
        if (event.type() == null || event.ownerId() == null || event.taskId() == null) {
            throw new AmqpRejectAndDontRequeueException("Invalid task event: " + event);
        }
        log.info("{} for task {} of user {}", event.type(), event.taskId(), event.ownerId());
        store.add(event.ownerId(), Notification.from(event));
    }
}
