package com.soulin.api.auth.repository;

import com.soulin.api.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email);

    boolean existsByEmailAndVerifiedTrue(String email);

    @Modifying
    @Query("delete from EmailVerification ev where ev.email = :email")
    void deleteAllByEmail(@Param("email") String email);
}
