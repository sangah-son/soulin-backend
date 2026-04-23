package com.soulin.api.user.controller;

import com.soulin.api.global.jwt.CustomUserPrincipal;
import com.soulin.api.user.dto.*;
import com.soulin.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        ProfileResponse response = userService.getMyProfile(principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ){
        UpdateProfileResponse response = userService.updateMyProfile(principal.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<UpdatePasswordResponse> updateMyPassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdatePasswordRequest request
    ){
        UpdatePasswordResponse response = userService.updateMyPassword(principal.getUserId(), request);
        return ResponseEntity.ok(response);
    }
}
