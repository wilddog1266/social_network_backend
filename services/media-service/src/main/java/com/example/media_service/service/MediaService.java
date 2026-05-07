package com.example.media_service.service;

import com.example.common.exception.BadRequestException;
import com.example.common.exception.NotFoundException;
import com.example.common.exception.StorageException;
import com.example.common.security.CurrentUser;
import com.example.media_service.entity.MediaEntity;
import com.example.media_service.entity.enums.MediaStatus;
import com.example.media_service.entity.enums.MediaType;
import com.example.media_service.repository.MediaRepository;
import com.example.media_service.response.MediaResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.example.media_service.entity.enums.MediaStatus.FAILED;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    private final MinioClient minioClient;

    @Value("${app.minio.bucket}")
    private String bucket;

    @Value("${app.minio.public-url}")
    private String publicUrl;

    public MediaResponse upload(MultipartFile multipartFile, MediaType type) {
        Long currentUserId = getCurrentUser().userId();

        String contentType = validateAndGetContentType(multipartFile);

        String objectKey = buildObjectKey(type, currentUserId, contentType);

        String originalFileName = multipartFile.getOriginalFilename() == null ? "unknown" : multipartFile.getOriginalFilename();

        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setBucket(bucket);
        mediaEntity.setSize(multipartFile.getSize());
        mediaEntity.setObjectKey(objectKey);
        mediaEntity.setStatus(MediaStatus.UPLOADING);
        mediaEntity.setType(type);
        mediaEntity.setOriginalFileName(originalFileName);
        mediaEntity.setOwnerUserId(currentUserId);

        MediaEntity uploading = mediaRepository.save(mediaEntity);

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            uploading.setStatus(FAILED);
            mediaRepository.save(uploading);
            throw new StorageException("Failed to upload file");
        }

        uploading.setStatus(MediaStatus.READY);

        MediaEntity saved = mediaRepository.save(uploading);

        return entityToResponse(saved);
    }

    public MediaResponse getPublicById(Long id) {
        MediaEntity media = mediaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Media not found"));

        if(media.getStatus() != MediaStatus.READY) {
            throw new NotFoundException("Media is not available");
        }

        return entityToResponse(media);
    }

    public MediaResponse getById(Long id) {
        Long currentUserId = getCurrentUser().userId();

        MediaEntity media = mediaRepository.findByIdAndOwnerUserId(id, currentUserId)
                .orElseThrow(() -> new NotFoundException("Media not found or access denied"));

        if(media.getStatus() != MediaStatus.READY) {
            throw new NotFoundException("Media is not available");
        }

        return entityToResponse(media);
    }

    public void deleteById(Long id) {
        Long currentUserId = getCurrentUser().userId();

        MediaEntity toDelete = mediaRepository.findByIdAndOwnerUserId(id, currentUserId)
                .orElseThrow(() -> new NotFoundException("Media not found"));

        try {
            minioClient.removeObject(
                            RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(toDelete.getObjectKey())
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to delete media");
        }

        mediaRepository.delete(toDelete);
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String validateAndGetContentType(MultipartFile multipartFile) {
        if(multipartFile.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        long maxSize = 5 * 1024 * 1024;

        if(multipartFile.getSize() > maxSize) {
            throw new BadRequestException("File size exceeds 5Mb");
        }

        String contentType = multipartFile.getContentType();
        if(contentType == null
                || !contentType.equals("image/jpeg")
                && !contentType.equals("image/png")) {
            throw new BadRequestException("Only PNG or JPEG images are allowed");
        }

        return contentType;
    }

    private String buildObjectKey(MediaType type, Long currentUserId, String contentType) {
        String uuid = UUID.randomUUID().toString();

        switch (type) {
            case AVATAR:
                return "avatar/" + currentUserId + "/" + uuid + "." + resolveExtension(contentType);
            case POST_IMAGE:
                return "post/" + currentUserId + "/" + uuid + "." + resolveExtension(contentType);
            default:
                throw new BadRequestException("Unknown media type");
        }
    }

    private String resolveExtension(String contentType) {

        if(contentType.contains("jpeg")) return "jpg";
        if(contentType.contains("png")) return "png";

        throw new BadRequestException("Unsupported content type");
    }

    private MediaResponse entityToResponse(MediaEntity entity) {
        MediaResponse response = new MediaResponse();

        response.setBucket(entity.getBucket());
        response.setSize(entity.getSize());
        response.setType(entity.getType());
        response.setId(entity.getId());
        response.setStatus(entity.getStatus());
        response.setObjectKey(entity.getObjectKey());
        response.setOriginalFileName(entity.getOriginalFileName());
        response.setUrl(publicUrl + "/" + entity.getBucket() + "/" + entity.getObjectKey());

        return response;
    }

}
