package com.example.user_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicProfileResponse {

    private Long id;

    private String username;

    private String displayName;

    private Long avatarId;
}
