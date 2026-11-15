package com.flairs.taskapi.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank(message = "refreshToken is required") String refreshToken) {

    @Override
    public String toString() {
        return "RefreshRequest[***]";
    }
}
