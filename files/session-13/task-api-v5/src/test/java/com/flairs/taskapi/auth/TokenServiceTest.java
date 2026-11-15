package com.flairs.taskapi.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flairs.taskapi.model.RefreshToken;
import com.flairs.taskapi.model.Role;
import com.flairs.taskapi.model.User;
import com.flairs.taskapi.repository.RefreshTokenRepository;
import com.flairs.taskapi.security.JwtConfig;
import com.flairs.taskapi.security.JwtProperties;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

// The real encoder and decoder, a mocked repository. No Spring context.
class TokenServiceTest {

    static final JwtProperties PROPS = new JwtProperties("test-secret-test-secret-test-secret-123", "task-api",
            Duration.ofMinutes(15), Duration.ofDays(7));

    final JwtConfig config = new JwtConfig();
    final SecretKey key = config.jwtSecretKey(PROPS);
    final JwtDecoder decoder = config.jwtDecoder(key, PROPS);
    final RefreshTokenRepository repo = mock(RefreshTokenRepository.class);
    User sara;

    TokenService serviceAt(Instant now) {
        return new TokenService(config.jwtEncoder(key), PROPS, repo, Clock.fixed(now, ZoneOffset.UTC));
    }

    @BeforeEach
    void setUp() {
        sara = new User("sara@flairs.dev", "hash", "Sara", Role.USER);
        ReflectionTestUtils.setField(sara, "id", 2L);
    }

    @Test
    void theAccessTokenCarriesTheUserIdAndRole() {
        TokenResponse tokens = serviceAt(Instant.now()).issue(sara);

        Jwt jwt = decoder.decode(tokens.accessToken());
        assertThat(jwt.getSubject()).isEqualTo("2");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("USER");
        assertThat(tokens.expiresIn()).isEqualTo(900);
        verify(repo).save(any(RefreshToken.class));
    }

    @Test
    void anExpiredAccessTokenIsRejected() {
        String token = serviceAt(Instant.now().minus(Duration.ofHours(1))).issue(sara).accessToken();

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class).hasMessageContaining("expired");
    }

    @Test
    void aTamperedAccessTokenIsRejected() {
        String token = serviceAt(Instant.now()).issue(sara).accessToken();
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");

        assertThatThrownBy(() -> decoder.decode(tampered)).isInstanceOf(JwtException.class);
    }

    @Test
    void reusingARevokedRefreshTokenRevokesEverything() {
        Instant now = Instant.now();
        RefreshToken used = new RefreshToken(sara, "h", now, now.plus(Duration.ofDays(7)));
        used.revoke(now);
        when(repo.findByTokenHash(anyString())).thenReturn(Optional.of(used));

        assertThatThrownBy(() -> serviceAt(now).refresh("stolen")).isInstanceOf(InvalidCredentialsException.class);
        verify(repo).revokeAllOf(2L, now);
    }

    @Test
    void refreshRotatesTheToken() {
        Instant now = Instant.now();
        RefreshToken current = new RefreshToken(sara, "h", now, now.plus(Duration.ofDays(7)));
        when(repo.findByTokenHash(TokenService.hash("raw"))).thenReturn(Optional.of(current));

        TokenResponse next = serviceAt(now).refresh("raw");

        assertThat(current.getRevokedAt()).isEqualTo(now);
        assertThat(next.refreshToken()).isNotEqualTo("raw");
    }
}
