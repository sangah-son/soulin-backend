package com.soulin.api.auth.repository;

import com.soulin.api.auth.entity.PasswordResetVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetVerificationRepository extends JpaRepository<PasswordResetVerification, Long> {
    Optional<PasswordResetVerification> findTopByEmailOrderByIdDesc(String email);
    Optional<PasswordResetVerification> findByResetTokenHash(String resetTokenHash);
    long countByEmailAndCreatedAtAfter(String email, LocalDateTime createdAt);
}
