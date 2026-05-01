package com.soulin.api.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal {
    private Long userId;
    private String email;
    private String userName;
    private String sessionId;
}
