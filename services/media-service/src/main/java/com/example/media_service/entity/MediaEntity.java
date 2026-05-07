package com.example.media_service.entity;

import com.example.media_service.entity.enums.MediaStatus;
import com.example.media_service.entity.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(schema = "media", name = "medias")
@Getter
@Setter
public class MediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "object_key", nullable = false, unique = true)
    private String objectKey;

    @Column(name = "bucket", nullable = false)
    private String bucket;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MediaType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MediaStatus status;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

}
