package com.soulin.api.auth.service;

import com.soulin.api.auth.dto.*;
import com.soulin.api.auth.entity.RefreshToken;
import com.soulin.api.auth.repository.RefreshTokenRepository;
import com.soulin.api.global.jwt.JwtTokenProvider;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        String encodedPassword=passwordEncoder.encode(request.getPassword());
        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getUserName()
        );
        User savedUser = userRepository.save(user);
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUserName()
        );
    }

    public LoginResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("이메일 혹의 비밀번호가 올바르지 않습니다."));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 올바르지 않습니다.");
        }

        String sessionId= UUID.randomUUID().toString();

        String accessToken= jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                sessionId,
                user.getTokenVersion()
        );

        String refreshToken= jwtTokenProvider.createRefreshToken(sessionId);

        RefreshToken savedRefreshToken = new RefreshToken(
                sessionId,
                user,
                hashToken(refreshToken),
                LocalDateTime.now().plusNanos(refreshTokenExpiration * 1_000_000)
        );

        refreshTokenRepository.save(savedRefreshToken);

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUserName()
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);
        } catch (Exception e) {
            throw new IllegalStateException("토큰 해시에 실패했습니다.", e);
        }
    }

    public LogoutResponse logout(Long userId, String sessionId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RefreshToken refreshToken = refreshTokenRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("refresh token을 찾을 수 없습니다."));

        if (!refreshToken.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("로그아웃할 수 없는 세션입니다.");
        }

        refreshToken.revoke();

        return new LogoutResponse("로그아웃되었습니다.");
    }


    public TokenReissueResponse reissue(TokenReissueRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh token입니다.");
        }

        String sessionId = jwtTokenProvider.getSessionId(refreshToken);

        RefreshToken savedRefreshToken = refreshTokenRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("refresh token을 찾을 수 없습니다."));

        if (!savedRefreshToken.isActive(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료되었거나 로그아웃된 refresh token입니다.");
        }

        if (!savedRefreshToken.getTokenHash().equals(hashToken(refreshToken))) {
            savedRefreshToken.revoke();
            throw new IllegalArgumentException("refresh token 정보가 일치하지 않습니다.");
        }

        User user = savedRefreshToken.getUser();

        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                savedRefreshToken.getSessionId(),
                user.getTokenVersion()
        );

        String newRefreshToken = jwtTokenProvider.createRefreshToken(savedRefreshToken.getSessionId());

        savedRefreshToken.rotate(
                hashToken(newRefreshToken),
                LocalDateTime.now().plusNanos(refreshTokenExpiration * 1_000_000)
        );

        return new TokenReissueResponse(
                newAccessToken,
                newRefreshToken
        );
    }

}
