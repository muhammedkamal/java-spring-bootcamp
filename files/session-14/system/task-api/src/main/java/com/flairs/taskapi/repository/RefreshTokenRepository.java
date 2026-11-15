package com.flairs.taskapi.repository;

import com.flairs.taskapi.model.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // One UPDATE statement for all rows, instead of loading every token. @Modifying marks it as a write.
    @Modifying
    @Query("update RefreshToken r set r.revokedAt = :now where r.user.id = :userId and r.revokedAt is null")
    int revokeAllOf(@Param("userId") Long userId, @Param("now") Instant now);
}
