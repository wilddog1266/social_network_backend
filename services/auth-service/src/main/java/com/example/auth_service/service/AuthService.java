package com.example.auth_service.service;

import com.example.auth_service.config.JwtProperties;
import com.example.auth_service.entity.RefreshTokenEntity;
import com.example.auth_service.entity.UserEntity;
import com.example.auth_service.exception.InvalidCredentialsException;
import com.example.auth_service.exception.UserAlreadyExistsException;
import com.example.auth_service.repository.RefreshTokenRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.request.LoginRequest;
import com.example.auth_service.request.RefreshRequest;
import com.example.auth_service.request.RegisterRequest;
import com.example.auth_service.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthResponse register(RegisterRequest request) {
        UserEntity user = new UserEntity();
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String normalizedUsername = request.getUsername().trim().toLowerCase(Locale.ROOT);

        if(userRepository.existsByUsername(normalizedUsername)) {
            throw new UserAlreadyExistsException("User with username: '" + normalizedUsername + "' already exists");
        }

        if(userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("User with email: '" + normalizedEmail + "' already exists");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        user.setEmail(normalizedEmail);
        user.setUsername(normalizedUsername);
        user.setPasswordHash(passwordHash);

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedUsername = request.getUsername().trim().toLowerCase(Locale.ROOT);

        UserEntity user = userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String rawToken = request.getRefreshToken();

        String sha256 = sha256(rawToken);

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByTokenHash(sha256)
                .orElseThrow(InvalidCredentialsException::new);

        if (refreshTokenEntity.isRevoked()) {
            refreshTokenRepository.deleteAllByUserId(refreshTokenEntity.getUserId());
            throw new InvalidCredentialsException();
        }

        if (refreshTokenEntity.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidCredentialsException();
        }

        UserEntity user = userRepository.findById(refreshTokenEntity.getUserId())
                .orElseThrow(InvalidCredentialsException::new);

        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);

        String newRefreshToken = generateRefreshToken(user.getId());

        String accessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(accessToken, newRefreshToken);
    }

    public void logout(RefreshRequest request) {
        String rawToken = request.getRefreshToken();

        String sha256 = sha256(rawToken);

        refreshTokenRepository.findByTokenHash(sha256)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private String generateRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString() + UUID.randomUUID().toString();

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUserId(userId);
        refreshTokenEntity.setTokenHash(sha256(token));
        refreshTokenEntity.setExpiresAt(Instant.now().plus(jwtProperties.getRefreshTtlDays(), ChronoUnit.DAYS));
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

        return token;
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
