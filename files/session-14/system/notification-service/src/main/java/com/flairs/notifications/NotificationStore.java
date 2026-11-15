package com.flairs.notifications;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

// In memory on purpose: the session is about messaging, not another database. A restart forgets everything.
// The final project version would save notifications in its own PostgreSQL schema.
@Component
public class NotificationStore {

    static final int MAX_PER_USER = 50;

    private final Map<Long, Deque<Notification>> byOwner = new ConcurrentHashMap<>();

    public void add(Long ownerId, Notification notification) {
        Deque<Notification> list = byOwner.computeIfAbsent(ownerId, id -> new ArrayDeque<>());
        synchronized (list) {                 // the listener and the controller run on different threads
            list.addFirst(notification);      // newest first
            if (list.size() > MAX_PER_USER) {
                list.removeLast();
            }
        }
    }

    public List<Notification> forOwner(Long ownerId) {
        Deque<Notification> list = byOwner.get(ownerId);
        if (list == null) {
            return List.of();
        }
        synchronized (list) {
            return List.copyOf(list);
        }
    }
}
