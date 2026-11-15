package com.flairs.taskapi.auth;

import com.flairs.taskapi.model.RefreshToken;
import com.flairs.taskapi.model.User;
import com.flairs.taskapi.repository.RefreshTokenRepository;
import com.flairs.taskapi.security.JwtProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Two kinds of token:
//  - access token: a signed JWT, short-lived (15 min). The API checks the signature; no database lookup.
//  - refresh token: a long random string, long-lived (7 days), stored HASHED in the database so it can be revoked.
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final JwtEncoder encoder;
    private final JwtProperties props;
    private final RefreshTokenRepository refreshTokens;
    private final Clock clock;

    public TokenService(JwtEncoder encoder, JwtProperties props, RefreshTokenRepository refreshTokens, Clock clock) {
        this.encoder = encoder;
        this.props = props;
        this.refreshTokens = refreshTokens;
        this.clock = clock;
    }

    @Transactional
    public TokenResponse issue(User user) {
        return new TokenResponse(accessToken(user), "Bearer", props.accessTokenTtl().toSeconds(), newRefreshToken(user));
    }

    // Rotation: a refresh token works ONCE. Using it revokes it and returns a new pair.
    // If someone presents a token that was already revoked, it was probably stolen and used twice:
    // we revoke every token of that user, so both the thief and the user must log in again.
    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public TokenResponse refresh(String rawToken) {
        Instant now = clock.instant();
        RefreshToken stored = refreshTokens.findByTokenHash(hash(rawToken)).orElseThrow(InvalidCredentialsException::new);
        if (stored.getRevokedAt() != null) {
            int revoked = refreshTokens.revokeAllOf(stored.getUser().getId(), now);
            log.warn("Refresh token reuse for user {}: revoked {} tokens", stored.getUser().getId(), revoked);
            throw new InvalidCredentialsException();
        }
        if (!stored.isUsable(now)) {
            throw new InvalidCredentialsException();
        }
        stored.revoke(now);
        return issue(stored.getUser());
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokens.findByTokenHash(hash(rawToken)).ifPresent(token -> token.revoke(clock.instant()));
    }

    String accessToken(User user) {
        Instant now = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.issuer())
                .subject(user.getId().toString())             // sub = who: the user id
                .issuedAt(now)
                .expiresAt(now.plus(props.accessTokenTtl()))  // exp: the decoder rejects it after this
                .claim("email", user.getEmail())
                .claim("roles", List.of(user.getRole().name()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String newRefreshToken(User user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant now = clock.instant();
        refreshTokens.save(new RefreshToken(user, hash(raw), now, now.plus(props.refreshTokenTtl())));
        return raw;                                          // the only time the raw value exists
    }

    static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
