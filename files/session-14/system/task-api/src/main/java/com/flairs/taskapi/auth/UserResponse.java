package com.flairs.taskapi.auth;

import com.flairs.taskapi.model.Role;
import com.flairs.taskapi.model.User;
import java.time.Instant;

// No passwordHash here, ever.
public record UserResponse(Long id, String email, String displayName, Role role, Instant createdAt) {

    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole(), user.getCreatedAt());
    }
}
