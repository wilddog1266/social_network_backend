package com.example.notification_service.service;

import com.example.notification_service.entity.NotificationEntity;
import com.example.notification_service.exception.NotificationNotFoundException;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationResponse> userNotifications(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return notificationRepository.findByUserId(userId, pageRequest).map(this::entityToResponse);
    }

    public void markNotificationAsRead(Long notificationId) {
        NotificationEntity notificationEntity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notificationEntity.setRead(true);
        notificationRepository.save(notificationEntity);
    }


    private NotificationResponse entityToResponse(NotificationEntity entity) {
        return new NotificationResponse(entity.getId(), entity.getUserId(), entity.getActorId(), entity.getType(), entity.isRead(), entity.getCreatedAt());
    }
}
