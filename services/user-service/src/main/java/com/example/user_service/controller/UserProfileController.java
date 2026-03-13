package com.example.user_service.controller;

import com.example.user_service.request.CreateProfileRequest;
import com.example.user_service.request.UpdateProfileRequest;
import com.example.user_service.response.CreateProfileResponse;
import com.example.user_service.response.ProfileResponse;
import com.example.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<CreateProfileResponse> create(@Valid @RequestBody CreateProfileRequest request) {
        CreateProfileResponse response = userProfileService.createProfile(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(userProfileService.getMyProfile());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfileById(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(request));
    }
}
