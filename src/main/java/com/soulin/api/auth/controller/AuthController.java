package com.soulin.api.auth.controller;

import com.soulin.api.auth.dto.*;
import com.soulin.api.auth.service.AuthService;
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
