package com.soulin.api.global.config;

import com.soulin.api.auth.entity.RefreshToken;
import com.soulin.api.auth.repository.RefreshTokenRepository;
import com.soulin.api.global.jwt.CustomUserPrincipal;
import com.soulin.api.global.jwt.JwtTokenProvider;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            accessor.setUser(authenticate(accessor.getFirstNativeHeader("Authorization")));
        }
        return message;
    }

    private UsernamePasswordAuthenticationToken authenticate(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new MessagingException("웹소켓 연결에 Access Token이 필요합니다.");
        }

        String token = authorization.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new MessagingException("유효하지 않은 Access Token입니다.");
        }

        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MessagingException("사용자를 찾을 수 없습니다."));

        if (!user.getTokenVersion().equals(jwtTokenProvider.getTokenVersion(token))) {
            throw new MessagingException("만료된 사용자 세션입니다.");
        }

        String sessionId = jwtTokenProvider.getSessionIdFromAccessToken(token);
        RefreshToken refreshToken = refreshTokenRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new MessagingException("로그아웃된 세션입니다."));
        if (refreshToken.isRevoked()) {
            throw new MessagingException("로그아웃된 세션입니다.");
        }

        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getUserName(),
                sessionId
        );
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ) {
            @Override
            public String getName() {
                return String.valueOf(userId);
            }
        };
    }
}
