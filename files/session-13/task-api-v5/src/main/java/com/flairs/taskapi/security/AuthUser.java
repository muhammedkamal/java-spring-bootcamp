package com.flairs.taskapi.security;

import com.flairs.taskapi.model.Role;
import org.springframework.security.oauth2.jwt.Jwt;

// Who is calling, read from the verified JWT. Controllers pass it to services, so the
// service rules ("only the owner") are plain Java and easy to unit test.
public record AuthUser(Long id, String email, Role role) {

    public static AuthUser from(Jwt jwt) {
        return new AuthUser(Long.valueOf(jwt.getSubject()), jwt.getClaimAsString("email"),
                Role.valueOf(jwt.getClaimAsStringList("roles").getFirst()));
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    // For repository queries: null = no owner filter
    public Long ownerFilter() {
        return isAdmin() ? null : id;
    }
}
