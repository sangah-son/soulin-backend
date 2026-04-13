package com.soulin.api.auth.controller;

import com.soulin.api.auth.dto.SignupRequest;
import com.soulin.api.auth.dto.SignupResponse;
import com.soulin.api.auth.service.AuthService;
import com.soulin.api.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
