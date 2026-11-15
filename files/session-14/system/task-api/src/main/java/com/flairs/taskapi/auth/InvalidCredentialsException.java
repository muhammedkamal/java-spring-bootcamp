package com.flairs.taskapi.auth;

// One message for "no such email", "wrong password" and "bad refresh token":
// a different message would tell an attacker which emails exist.
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email, password or token");
    }
}
