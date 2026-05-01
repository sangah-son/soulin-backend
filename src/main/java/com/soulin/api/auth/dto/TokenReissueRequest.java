package com.soulin.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenReissueRequest {
    @NotBlank
    private String refreshToken;
}
