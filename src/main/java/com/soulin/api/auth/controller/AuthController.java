package com.soulin.api.auth.controller;

import com.soulin.api.auth.dto.*;
import com.soulin.api.auth.service.AuthService;
import com.soulin.api.auth.service.EmailVerificationService;
import com.soulin.api.auth.service.PasswordResetService;
import com.soulin.api.global.jwt.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/password-reset/send-code")
    public ResponseEntity<MessageResponse> sendPasswordResetCode(
            @Valid @RequestBody PasswordResetSendCodeRequest request
    ) {
        passwordResetService.sendCode(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("입력한 이메일로 인증번호를 발송했습니다."));
    }

    @PostMapping("/password-reset/verify-code")
    public ResponseEntity<PasswordResetVerifyCodeResponse> verifyPasswordResetCode(
            @Valid @RequestBody PasswordResetVerifyCodeRequest request
    ) {
        return ResponseEntity.ok(passwordResetService.verifyCode(request.getEmail(), request.getCode()));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("비밀번호가 재설정되었습니다."));
    }

    @PostMapping("/email/send-code")
    public ResponseEntity<Void> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        emailVerificationService.sendCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response=authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response=authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        LogoutResponse response = authService.logout(
                principal.getUserId(),
                principal.getSessionId()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueResponse> reissue(
            @Valid @RequestBody TokenReissueRequest request
    ){
        TokenReissueResponse response=authService.reissue(request);
        return ResponseEntity.ok(response);
    }
}
