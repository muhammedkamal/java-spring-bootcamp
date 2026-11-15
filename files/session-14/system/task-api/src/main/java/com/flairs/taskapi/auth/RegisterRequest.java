package com.flairs.taskapi.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "email is required") @Email(message = "email must be a valid address")
        @Size(max = 120, message = "email must be at most 120 characters") String email,
        // BCrypt only uses the first 72 bytes, so we cap the length
        @NotBlank(message = "password is required")
        @Size(min = 8, max = 72, message = "password must be 8 to 72 characters") String password,
        @NotBlank(message = "displayName is required")
        @Size(max = 80, message = "displayName must be at most 80 characters") String displayName) {

    // Never print the password, not even in a log line or a debugger
    @Override
    public String toString() {
        return "RegisterRequest[email=" + email + ", displayName=" + displayName + "]";
    }
}
