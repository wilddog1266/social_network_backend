package com.example.user_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class ProfileResponse {

    private Long userId;

    private String username;

    private String displayName;

    private String bio;

    private String avatarUrl;

    private Instant createdAt;
}
