package com.flairs.taskapi.auth;

import com.flairs.taskapi.security.AuthUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService auth;
    private final TokenService tokens;

    public AuthController(AuthService auth, TokenService tokens) {
        this.auth = auth;
        this.tokens = tokens;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = auth.register(request);
        return ResponseEntity.created(URI.create("/api/auth/me")).body(user);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return tokens.refresh(request.refreshToken());
    }

    // Needs a valid access token. The access token itself stays valid until it expires (max 15 min);
    // that is the price of stateless JWTs and the reason access tokens are short-lived.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        tokens.revoke(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    // @AuthenticationPrincipal gives the verified Jwt of the current request
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return auth.me(AuthUser.from(jwt).id());
    }
}
