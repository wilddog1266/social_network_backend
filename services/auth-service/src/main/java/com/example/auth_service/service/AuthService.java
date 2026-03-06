package com.example.auth_service.service;

import com.example.auth_service.entity.UserEntity;
import com.example.auth_service.exception.UserAlreadyExistsException;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.request.RegisterRequest;
import com.example.auth_service.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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

        return new AuthResponse("stub", "stub");
    }
}
