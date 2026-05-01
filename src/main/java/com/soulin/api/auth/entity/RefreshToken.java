package com.soulin.api.auth.entity;

import com.soulin.api.global.common.BaseEntity;
import com.soulin.api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    //로그인 한 번을 세션 한 개라고 침
    @Column(name = "session_id", nullable = false, unique = true, length = 36)
    private String sessionId;

    //user 1명이 여러 기기에서 로그인 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //refresh token 원본을 해시해서 DB에 저장
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    //refresh token 만료 시간(14일 뒤)
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    //이 토큰이 무효화 됐는지 표시(ex 로그아웃 시 true)
    @Column(nullable = false)
    private boolean revoked = false;

    public RefreshToken(String sessionId, User user, String tokenHash, LocalDateTime expiresAt) {
        this.sessionId = sessionId;
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    //해당 토큰이 아직 쓸 수 있나 검사
    public boolean isActive(LocalDateTime now) {
        return !revoked && expiresAt.isAfter(now);
    }

    //rotation용 메서드, 재발급할 때마다 token 새로 바꿈
    public void rotate(String tokenHash, LocalDateTime expiresAt) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    //해당 토큰 더 이상 못 쓰게 함(로그아웃 시)
    public void revoke() {
        this.revoked = true;
    }
}

//refresh token DB에 저장하고, 나중에 찾아서 검증/무효화/교체화 할 수 있는 구조 만듦
