package com.flairs.taskapi.auth;

// What the client stores after login. It sends "Authorization: Bearer <accessToken>" on every call,
// and uses refreshToken to get a new pair when the access token expires (expiresIn seconds).
public record TokenResponse(String accessToken, String tokenType, long expiresIn, String refreshToken) {
}
