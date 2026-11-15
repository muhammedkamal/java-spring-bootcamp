package com.flairs.notifications;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationStore store;

    public NotificationController(NotificationStore store) {
        this.store = store;
    }

    // Only YOUR notifications: the user id comes from the verified token ("sub"), never from the URL
    @GetMapping
    public List<Notification> mine(@AuthenticationPrincipal Jwt jwt) {
        return store.forOwner(Long.valueOf(jwt.getSubject()));
    }
}
