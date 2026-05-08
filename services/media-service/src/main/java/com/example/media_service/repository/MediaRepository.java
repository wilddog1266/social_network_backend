package com.example.media_service.repository;

import com.example.media_service.entity.MediaEntity;
import com.example.media_service.entity.enums.MediaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {

    Optional<MediaEntity> findById(Long id);

    Page<MediaEntity> findByOwnerUserId(Long ownerUserId, Pageable pageable);

    Optional<MediaEntity> findByObjectKey(String objectKey);

    Optional<MediaEntity> findByIdAndOwnerUserId(Long id, Long ownerUserId);

    boolean existsByObjectKey(String objectKey);

    Page<MediaEntity> findByStatus(MediaStatus status, Pageable pageable);

    Page<MediaEntity> findByOwnerUserIdAndStatus(Long ownerUserId, MediaStatus status, Pageable pageable);

    List<MediaEntity> findAllByIdIn(List<Long> ids);

    List<MediaEntity> findByIdInAndStatus(List<Long> ids, MediaStatus status);
}
