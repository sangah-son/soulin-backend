package com.soulin.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordResetSendCodeRequest {
    @NotBlank
    @Email
    private String email;
}
