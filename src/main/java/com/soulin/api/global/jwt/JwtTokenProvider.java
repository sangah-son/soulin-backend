package com.soulin.api.global.jwt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secretKey.getBytes()));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, String email, String sessionId, Long tokenVersion) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("sessionId", sessionId)
                .claim("tokenVersion", tokenVersion)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String sessionId){
        Date now=new Date();
        Date expiry=new Date(now.getTime()+refreshTokenExpiration);

        return Jwts.builder()
                .subject(sessionId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String getSessionId(String token){
        Claims claims=parseClaims(token);
        return claims.getSubject();
    }

    public String getSessionIdFromAccessToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("sessionId", String.class);
    }

    public Long getTokenVersion(String token) {
        Claims claims = parseClaims(token);
        return claims.get("tokenVersion", Long.class);
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getEmail(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
