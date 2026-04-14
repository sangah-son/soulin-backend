package com.soulin.api.user.controller;

import com.soulin.api.user.dto.*;
import com.soulin.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@RequestParam Long userId){
        ProfileResponse response=userService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateMyProfile(
            @RequestParam Long userId,
            @Valid @RequestBody UpdateProfileRequest request
            ){
        UpdateProfileResponse response=userService.updateMyProfile(userId,request);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/me/password")
    public ResponseEntity<UpdatePasswordResponse> updateMyPassword(@RequestParam Long userId, @Valid @RequestBody UpdatePasswordRequest request){
        UpdatePasswordResponse response=userService.updateMyPassword(userId,request);
        return ResponseEntity.ok(response);
    }
}
