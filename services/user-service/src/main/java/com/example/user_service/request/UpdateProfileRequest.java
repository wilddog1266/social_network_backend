package com.example.user_service.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(min = 3, max = 100)
    private String displayName;

    @Size(max = 500)
    private String bio;

    private String avatarUrl;
}
