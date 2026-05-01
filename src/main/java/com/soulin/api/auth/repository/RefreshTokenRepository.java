package com.soulin.api.auth.repository;

import com.soulin.api.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findBySessionId(String sessionId);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.user.id = :userId and rt.revoked = false")
    void revokeAllByUserId(@Param("userId") Long userId);
}
