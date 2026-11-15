package com.flairs.notifications;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class NotificationStoreTest {

    NotificationStore store = new NotificationStore();

    @Test
    void eachUserSeesOnlyTheirOwnNewestFirst() {
        store.add(2L, new Notification(1L, "first", Instant.EPOCH));
        store.add(2L, new Notification(2L, "second", Instant.EPOCH));
        store.add(3L, new Notification(3L, "omar's", Instant.EPOCH));

        assertThat(store.forOwner(2L)).extracting(Notification::message).containsExactly("second", "first");
        assertThat(store.forOwner(3L)).hasSize(1);
        assertThat(store.forOwner(99L)).isEmpty();
    }

    @Test
    void keepsOnlyTheLatestFifty() {
        for (long i = 1; i <= 60; i++) {
            store.add(2L, new Notification(i, "n" + i, Instant.EPOCH));
        }

        assertThat(store.forOwner(2L)).hasSize(NotificationStore.MAX_PER_USER)
                .first().extracting(Notification::taskId).isEqualTo(60L);
    }
}
