package com.example.feed_service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class FollowingResponse {

    private Long userId;
    private Instant createdAt;
}
