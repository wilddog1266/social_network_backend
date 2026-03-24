package com.example.notification_service.service;

import com.example.common.security.CurrentUser;
import com.example.notification_service.entity.NotificationEntity;
import com.example.notification_service.exception.NotificationNotFoundException;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationResponse> userNotifications(int page, int size) {
        CurrentUser currentUser = getCurrentUser();

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return notificationRepository.findByUserId(currentUser.userId(), pageRequest).map(this::entityToResponse);
    }

    public void markNotificationAsRead(Long id) {
        CurrentUser currentUser = getCurrentUser();

        NotificationEntity notificationEntity = notificationRepository.findByIdAndUserId(id, currentUser.userId())
                .orElseThrow(() -> new NotificationNotFoundException(id));

        notificationEntity.setRead(true);
        notificationRepository.save(notificationEntity);
    }

    public void markAllAsRead() {
        CurrentUser currentUser = getCurrentUser();

        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIsReadFalse(currentUser.userId());

        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }


    private NotificationResponse entityToResponse(NotificationEntity entity) {
        return new NotificationResponse(entity.getId(), entity.getUserId(), entity.getActorId(), entity.getType(), entity.isRead(), entity.getCreatedAt());
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
