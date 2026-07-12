package com.soulin.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordResetRequest {
    @NotBlank
    private String resetToken;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String newPasswordConfirm;
}
