package com.flairs.libraryapi.auth;

import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.security.JwtProperties;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtProperties props;
    private final Clock clock;

    public TokenService(JwtEncoder encoder, JwtProperties props, Clock clock) {
        this.encoder = encoder;
        this.props = props;
        this.clock = clock;
    }

    // TODO 2: build and sign the access token.
    //  - JwtClaimsSet with: issuer = props.issuer(), subject = the member id as a String,
    //    issuedAt = now, expiresAt = now + props.accessTokenTtl(),
    //    claim "email" = member email, claim "roles" = List.of(member.getRole().name())
    //  - header: JwsHeader.with(MacAlgorithm.HS256).build()
    //  - encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue()
    //  - return new TokenResponse(token, "Bearer", ttl in seconds)
    // Use clock.instant() for "now", never Instant.now(): the tests use a fixed clock.
    public TokenResponse issue(Member member) {
        throw new UnsupportedOperationException("TODO 2");
    }
}
