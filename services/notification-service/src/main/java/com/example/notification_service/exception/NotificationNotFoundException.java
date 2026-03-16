package com.example.notification_service.exception;

import com.example.common.exception.NotFoundException;

public class NotificationNotFoundException extends NotFoundException {
    public NotificationNotFoundException(Long notificationId) {
        super("Notification with id '" + notificationId + "' does not exists");
    }
}
