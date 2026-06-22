package com.soulin.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SendVerificationCodeRequest {
    @NotBlank
    @Email
    private String email;
}
