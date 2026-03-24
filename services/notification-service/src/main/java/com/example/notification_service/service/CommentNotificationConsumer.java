package com.example.notification_service.service;

import com.example.common.PostCommentedEvent;
import com.example.notification_service.entity.NotificationEntity;
import com.example.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentNotificationConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(
            topics = "${app.kafka.topic.post-commented}",
            containerFactory = "postCommentedKafkaListenerContainerFactory"
    )
    public void handle(PostCommentedEvent event) {
        if (event.getPostAuthorId().equals(event.getCommentAuthorId())) {
            return;
        }

        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setUserId(event.getPostAuthorId());
        notificationEntity.setActorId(event.getCommentAuthorId());
        notificationEntity.setType("COMMENTED");
        notificationEntity.setRead(false);

        notificationRepository.save(notificationEntity);
    }
}
