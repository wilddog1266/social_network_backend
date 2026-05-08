package com.example.user_service.service;

import com.example.common.exception.BadRequestException;
import com.example.common.security.CurrentUser;
import com.example.user_service.client.MediaClient;
import com.example.user_service.entity.UserProfileEntity;
import com.example.user_service.exception.ProfileAlreadyExistsException;
import com.example.user_service.exception.ProfileNotFoundException;
import com.example.user_service.repository.UserProfileRepository;
import com.example.user_service.request.CreateProfileRequest;
import com.example.user_service.request.PublicBunchProfileRequest;
import com.example.user_service.request.UpdateProfileRequest;
import com.example.user_service.response.CreateProfileResponse;
import com.example.user_service.response.ProfileResponse;
import com.example.user_service.response.PublicProfileResponse;
import com.example.user_service.response.media.MediaClientResponse;
import com.example.user_service.response.media.enums.MediaStatus;
import com.example.user_service.response.media.enums.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final MediaClient mediaClient;

    public CreateProfileResponse createProfile(CreateProfileRequest request) {
        CurrentUser currentUser = getCurrentUser();
        String normalizedUsername = currentUser.username().trim().toLowerCase(Locale.ROOT);

        if(userProfileRepository.existsByUserId(currentUser.userId())) {
            throw new ProfileAlreadyExistsException("Profile already exists");
        }

        UserProfileEntity userProfileEntity = new UserProfileEntity();

        if(request.getAvatarId() != null) {
           MediaClientResponse mediaClientResponse = mediaClient.getMediaById(request.getAvatarId());
           if(!mediaClientResponse.getStatus().equals(MediaStatus.READY) || !mediaClientResponse.getType().equals(MediaType.AVATAR)) {
               throw new BadRequestException("Invalid avatar media");
           }
        }

        userProfileEntity.setUserId(currentUser.userId());
        userProfileEntity.setUsername(normalizedUsername);
        userProfileEntity.setBio(request.getBio());
        userProfileEntity.setAvatarId(request.getAvatarId());
        userProfileEntity.setDisplayName(request.getDisplayName().trim());

        UserProfileEntity saved = userProfileRepository.save(userProfileEntity);

        return new CreateProfileResponse(saved.getUserId(), normalizedUsername, saved.getDisplayName(), saved.getBio(), saved.getAvatarId(), saved.getCreatedAt());
    }

    public ProfileResponse getMyProfile() {
        CurrentUser currentUser = getCurrentUser();
        UserProfileEntity entity = findOrCreateCurrentProfile(currentUser);

        return entityToResponse(entity);
    }

    public PublicProfileResponse getPublicProfileById(Long userId) {
        UserProfileEntity entity = userProfileRepository.findByUserId(userId)
                .orElseThrow(ProfileNotFoundException::new);

        return entityToPublicProfileResponse(entity);
    }

    public List<PublicProfileResponse> getPublicBunchProfilesByIds(PublicBunchProfileRequest request) {
        return userProfileRepository.findAllByIdIn(request.getIds())
                .stream()
                .distinct()
                .map(this::entityToPublicProfileResponse)
                .toList();
    }

    public ProfileResponse getProfileById(Long userId) {
        UserProfileEntity entity = userProfileRepository.findByUserId(userId)
                .orElseThrow(ProfileNotFoundException::new);

        return entityToResponse(entity);
    }

    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        CurrentUser currentUser = getCurrentUser();
        UserProfileEntity entity = findOrCreateCurrentProfile(currentUser);

        if(request.getAvatarId() != null) {
            MediaClientResponse mediaClientResponse = mediaClient.getMediaById(request.getAvatarId());
            if(!mediaClientResponse.getStatus().equals(MediaStatus.READY) || !mediaClientResponse.getType().equals(MediaType.AVATAR)) {
                throw new BadRequestException("Invalid avatar media");
            } else {
                entity.setAvatarId(request.getAvatarId());
            }
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

    private UserProfileEntity findOrCreateCurrentProfile(CurrentUser currentUser) {
        return userProfileRepository.findByUserId(currentUser.userId())
                .orElseGet(() -> {
                    String normalizedUsername = currentUser.username().trim().toLowerCase(Locale.ROOT);

                    UserProfileEntity entity = new UserProfileEntity();
                    entity.setUserId(currentUser.userId());
                    entity.setUsername(normalizedUsername);
                    entity.setDisplayName(currentUser.username().trim());
                    entity.setBio("");
                    entity.setAvatarId(null);

                    return userProfileRepository.save(entity);
                });
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private ProfileResponse entityToResponse(UserProfileEntity entity) {
        return new ProfileResponse(entity.getUserId(), entity.getUsername(), entity.getDisplayName(), entity.getBio(), entity.getAvatarId(), entity.getCreatedAt());
    }

    private PublicProfileResponse entityToPublicProfileResponse(UserProfileEntity entity) {
        return new PublicProfileResponse(entity.getId(), entity.getUsername(), entity.getDisplayName(), entity.getAvatarId());
    }
}
