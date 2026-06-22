package com.soulin.api.auth.service;

import com.soulin.api.auth.entity.EmailVerification;
import com.soulin.api.auth.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender mailSender;

    public void sendCode(String email) {
        // 기존 인증 내역 삭제
        emailVerificationRepository.deleteAllByEmail(email);

        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        emailVerificationRepository.save(new EmailVerification(email, code, expiresAt));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seumyodeulm@gmail.com");
        message.setTo(email);
        message.setSubject("[스며듦] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n\n5분 이내에 입력해주세요.");
        mailSender.send(message);
    }

    public void verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드를 먼저 발송해주세요."));

        if (verification.isExpired()) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다. 다시 발송해주세요.");
        }
        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        verification.verify();
    }

    @Transactional(readOnly = true)
    public boolean isVerified(String email) {
        return emailVerificationRepository.existsByEmailAndVerifiedTrue(email);
    }

    public void deleteVerification(String email) {
        emailVerificationRepository.deleteAllByEmail(email);
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
