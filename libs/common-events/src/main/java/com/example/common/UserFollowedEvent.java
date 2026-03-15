package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFollowedEvent {

    private Long followerId;

    private Long followeeId;

    private Instant createdAt;
}
