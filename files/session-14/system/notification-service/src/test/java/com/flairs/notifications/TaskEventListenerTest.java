package com.flairs.notifications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

// The listener is a plain method: unit test it without RabbitMQ
class TaskEventListenerTest {

    NotificationStore store = new NotificationStore();
    TaskEventListener listener = new TaskEventListener(store);

    @Test
    void aCompletedEventBecomesANotificationForTheOwner() {
        listener.on(new TaskEvent("COMPLETED", 5L, "Learn Docker", 2L, Instant.EPOCH));

        assertThat(store.forOwner(2L)).singleElement()
                .extracting(Notification::message).isEqualTo("Done: Learn Docker");
    }

    @Test
    void anUnknownTypeStillWorks() {
        listener.on(new TaskEvent("ARCHIVED", 5L, "Old", 2L, Instant.EPOCH));

        assertThat(store.forOwner(2L).getFirst().message()).isEqualTo("ARCHIVED: Old");
    }

    @Test
    void anEventWithoutOwnerIsRejectedNotRetried() {
        assertThatThrownBy(() -> listener.on(new TaskEvent("CREATED", 5L, "T", null, Instant.EPOCH)))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class);
    }
}
