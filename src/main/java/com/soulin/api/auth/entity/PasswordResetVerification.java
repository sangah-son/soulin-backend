package com.soulin.api.auth.entity;

import com.soulin.api.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "password_reset_verifications",
        indexes = @Index(name = "idx_password_reset_email_created", columnList = "email, created_at")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(name = "code_expires_at", nullable = false)
    private LocalDateTime codeExpiresAt;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "reset_token_hash", unique = true, length = 64)
    private String resetTokenHash;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(nullable = false)
    private boolean used;

    public PasswordResetVerification(String email, String codeHash, LocalDateTime codeExpiresAt) {
        this.email = email;
        this.codeHash = codeHash;
        this.codeExpiresAt = codeExpiresAt;
    }

    public boolean isCodeExpired(LocalDateTime now) {
        return now.isAfter(codeExpiresAt);
    }

    public void increaseAttempts() {
        attempts++;
    }

    public void verify(String resetTokenHash, LocalDateTime tokenExpiresAt) {
        this.verified = true;
        this.resetTokenHash = resetTokenHash;
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public boolean canReset(LocalDateTime now) {
        return verified && !used && tokenExpiresAt != null && tokenExpiresAt.isAfter(now);
    }

    public void use() {
        this.used = true;
    }
}
