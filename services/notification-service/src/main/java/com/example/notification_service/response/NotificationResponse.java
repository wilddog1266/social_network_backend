package com.example.notification_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long id;

    private Long userId;

    private Long actorId;

    private String type;

    private boolean read;

    private Instant createdAt;
}
