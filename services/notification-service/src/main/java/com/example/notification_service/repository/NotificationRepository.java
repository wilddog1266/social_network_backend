package com.example.notification_service.repository;

import com.example.notification_service.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface NotificationRepository  extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserId(Long userId, Pageable pageable);
}
