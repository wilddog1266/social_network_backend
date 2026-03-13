package com.example.common.security.service;

import com.example.common.security.CurrentUser;
import com.example.common.security.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;

public class JwtService {

    private final JwtProperties properties;
    private SecretKey secretKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CurrentUser extractCurrentUser(String token) {
        Claims claims = extractClaims(token);

        Long userId = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);

        return new CurrentUser(userId, username);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}