package com.example.notification_service.service;

import com.example.common.PostReactionAddedEvent;
import com.example.common.ReactionType;
import com.example.notification_service.entity.NotificationEntity;
import com.example.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionNotificationConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(
            topics = "${app.kafka.topic.post-reaction-added}",
            containerFactory = "postReactionAddedKafkaListenerContainerFactory"
    )
    public void handle(PostReactionAddedEvent event) {
        if (event.getPostAuthorId().equals(event.getReactionAuthorId())) {
            return;
        }

        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setUserId(event.getPostAuthorId());
        notificationEntity.setActorId(event.getReactionAuthorId());

        if (event.getReactionType() == ReactionType.LIKE) {
            notificationEntity.setType("POST_LIKED");
        } else {
            notificationEntity.setType("POST_DISLIKED");
        }

        notificationEntity.setRead(false);

        notificationRepository.save(notificationEntity);
    }
}