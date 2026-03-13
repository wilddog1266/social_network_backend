package com.example.user_service.service;

import com.example.common.security.CurrentUser;
import com.example.user_service.entity.UserProfileEntity;
import com.example.user_service.exception.ProfileAlreadyExistsException;
import com.example.user_service.exception.ProfileNotFoundException;
import com.example.user_service.repository.UserProfileRepository;
import com.example.user_service.request.CreateProfileRequest;
import com.example.user_service.request.UpdateProfileRequest;
import com.example.user_service.response.CreateProfileResponse;
import com.example.user_service.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

        return entityToResponse(entity);
    }

    public ProfileResponse getProfileById(Long userId) {
        UserProfileEntity entity = userProfileRepository.findByUserId(userId)
                .orElseThrow(ProfileNotFoundException::new);

        return entityToResponse(entity);
    }

    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        CurrentUser currentUser = getCurrentUser();

        UserProfileEntity entity = userProfileRepository.findByUserId(currentUser.userId())
                .orElseThrow(ProfileNotFoundException::new);

        if(request.getAvatarUrl() != null) {
            entity.setAvatarUrl(request.getAvatarUrl());
        }

        if(request.getBio() != null && !request.getBio().trim().isBlank()) {
            entity.setBio(request.getBio().trim());
        }

        if(request.getDisplayName() != null && !request.getDisplayName().trim().isBlank()) {
            entity.setDisplayName(request.getDisplayName().trim());
        }

        userProfileRepository.save(entity);

        return entityToResponse(entity);
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private ProfileResponse entityToResponse(UserProfileEntity entity) {
        return new ProfileResponse(entity.getUserId(), entity.getUsername(), entity.getDisplayName(), entity.getBio(), entity.getAvatarUrl(), entity.getCreatedAt());
    }
}
