package com.example.user_service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProfileRequest {

    @Size(max = 500)
    private String bio;

    @NotBlank
    @Size(min = 3, max = 100)
    private String displayName;

    private String avatarUrl;

}
