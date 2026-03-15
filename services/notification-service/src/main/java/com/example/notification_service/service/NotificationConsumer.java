package com.example.notification_service.service;

import com.example.common.UserFollowedEvent;
import com.example.notification_service.entity.NotificationEntity;
import com.example.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "user-followed")
    public void handle(UserFollowedEvent event) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setActorId(event.getFollowerId());
        notificationEntity.setUserId(event.getFolloweeId());
        notificationEntity.setType("FOLLOWED");
        notificationEntity.setRead(false);

        notificationRepository.save(notificationEntity);
    }
}
