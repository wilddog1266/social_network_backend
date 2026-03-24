package com.example.notification_service.repository;

import com.example.notification_service.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository  extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserId(Long userId, Pageable pageable);

    Optional<NotificationEntity> findByIdAndUserId(Long id, Long userId);

    List<NotificationEntity> findByUserIdAndIsReadFalse(Long userId);
}
