package com.example.notification_service.controller;

import com.example.common.security.CurrentUser;
import com.example.notification_service.response.NotificationResponse;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Page<NotificationResponse> getNotifications(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "15") int size) {
        return notificationService.userNotifications(getCurrentUserId(), page, size);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);

        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user in security context");
        }

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        return currentUser.userId();
    }
}
