package com.example.user_service.service;

import com.example.user_service.entity.UserProfileEntity;
import com.example.user_service.exception.ProfileAlreadyExistsException;
import com.example.user_service.exception.ProfileNotFoundException;
import com.example.user_service.repository.UserProfileRepository;
import com.example.user_service.request.CreateProfileRequest;
import com.example.user_service.response.CreateProfileResponse;
import com.example.user_service.response.ProfileResponse;
import com.example.user_service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public CreateProfileResponse createProfile(CreateProfileRequest request) {
        CurrentUser currentUser = getCurrentUser();
        String normalizedUsername = currentUser.username().trim().toLowerCase(Locale.ROOT);

        if(userProfileRepository.existsByUserId(currentUser.userId())) {
            throw new ProfileAlreadyExistsException("Profile already exists");
        }

        UserProfileEntity userProfileEntity = new UserProfileEntity();

        userProfileEntity.setUserId(currentUser.userId());
        userProfileEntity.setUsername(normalizedUsername);
        userProfileEntity.setBio(request.getBio());
        userProfileEntity.setAvatarUrl(request.getAvatarUrl());
        userProfileEntity.setDisplayName(request.getDisplayName().trim());

        UserProfileEntity saved = userProfileRepository.save(userProfileEntity);

        return new CreateProfileResponse(saved.getUserId(), normalizedUsername, saved.getDisplayName(), saved.getBio(), saved.getAvatarUrl(), saved.getCreatedAt());
    }

    public ProfileResponse getMyProfile() {
        CurrentUser currentUser = getCurrentUser();

        UserProfileEntity entity = userProfileRepository.findByUserId(currentUser.userId())
                .orElseThrow(ProfileNotFoundException::new);

        return new ProfileResponse(entity.getUserId(), entity.getUsername(), entity.getDisplayName(), entity.getBio(), entity.getAvatarUrl(), entity.getCreatedAt());
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
