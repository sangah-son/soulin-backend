package com.soulin.api.auth.service;

import com.soulin.api.auth.dto.PasswordResetRequest;
import com.soulin.api.auth.dto.PasswordResetVerifyCodeResponse;
import com.soulin.api.auth.entity.PasswordResetVerification;
import com.soulin.api.auth.repository.PasswordResetVerificationRepository;
import com.soulin.api.auth.repository.RefreshTokenRepository;
import com.soulin.api.global.exception.ApiException;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_SENDS_PER_HOUR = 5;
    private static final long RESEND_COOLDOWN_SECONDS = 60;
    private static final long RESET_TOKEN_EXPIRES_SECONDS = 600;

    private final PasswordResetVerificationRepository verificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    public void sendCode(String rawEmail) {
        String email = normalizeEmail(rawEmail);
        LocalDateTime now = LocalDateTime.now();

        verificationRepository.findTopByEmailOrderByIdDesc(email).ifPresent(latest -> {
            if (latest.getCreatedAt().plusSeconds(RESEND_COOLDOWN_SECONDS).isAfter(now)) {
                throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "VERIFICATION_RESEND_LIMIT",
                        "잠시 후 다시 시도해주세요.");
            }
        });

        if (verificationRepository.countByEmailAndCreatedAtAfter(email, now.minusHours(1)) >= MAX_SENDS_PER_HOUR) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "VERIFICATION_RESEND_LIMIT",
                    "잠시 후 다시 시도해주세요.");
        }

        // 가입 여부가 API 응답으로 노출되지 않도록 미가입 이메일도 정상 종료한다.
        if (!userRepository.existsByEmail(email)) {
            return;
        }

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        verificationRepository.save(new PasswordResetVerification(
                email,
                passwordEncoder.encode(code),
                now.plusMinutes(5)
        ));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(email);
        message.setSubject("[스며듦] 비밀번호 재설정 인증 코드");
        message.setText("인증 코드: " + code + "\n\n5분 이내에 입력해주세요.");
        mailSender.send(message);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public PasswordResetVerifyCodeResponse verifyCode(String rawEmail, String code) {
        String email = normalizeEmail(rawEmail);
        PasswordResetVerification verification = verificationRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(this::invalidCode);

        LocalDateTime now = LocalDateTime.now();
        if (verification.isCodeExpired(now)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_EXPIRED",
                    "인증번호가 만료되었습니다. 다시 요청해주세요.");
        }
        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "VERIFICATION_ATTEMPT_LIMIT",
                    "인증 시도 횟수를 초과했습니다. 인증번호를 다시 요청해주세요.");
        }
        if (verification.isVerified() || verification.isUsed()) {
            throw invalidCode();
        }
        if (!passwordEncoder.matches(code, verification.getCodeHash())) {
            verification.increaseAttempts();
            throw invalidCode();
        }

        String resetToken = generateResetToken();
        verification.verify(hash(resetToken), now.plusSeconds(RESET_TOKEN_EXPIRES_SECONDS));
        return new PasswordResetVerifyCodeResponse(resetToken, RESET_TOKEN_EXPIRES_SECONDS);
    }

    public void resetPassword(PasswordResetRequest request) {
        if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 72) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD_FORMAT",
                    "비밀번호는 6자 이상 72자 이하로 입력해주세요.");
        }
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PASSWORD_CONFIRM_MISMATCH",
                    "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        PasswordResetVerification verification = verificationRepository
                .findByResetTokenHash(hash(request.getResetToken()))
                .orElseThrow(this::invalidResetToken);

        if (!verification.canReset(LocalDateTime.now())) {
            throw invalidResetToken();
        }

        User user = userRepository.findByEmail(verification.getEmail())
                .orElseThrow(this::invalidResetToken);
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        user.increaseTokenVersion();
        refreshTokenRepository.revokeAllByUserId(user.getId());
        verification.use();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("토큰 해시에 실패했습니다.", e);
        }
    }

    private ApiException invalidCode() {
        return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE",
                "인증번호가 올바르지 않습니다.");
    }

    private ApiException invalidResetToken() {
        return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_RESET_TOKEN",
                "비밀번호 재설정 인증이 만료되었습니다. 다시 인증해주세요.");
    }
}
