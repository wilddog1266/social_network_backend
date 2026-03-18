package com.example.post_service.service;

import com.example.common.exception.BadRequestException;
import com.example.post_service.dto.UploadedFile;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostMediaStorageService {

    @Value("${app.minio.bucket}")
    private String bucket;

    private final MinioClient minioClient;

    public UploadedFile upload(Long postId, MultipartFile multipartFile) {
        if(multipartFile.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null) {
            originalFileName = "file";
        }

        String uuid = UUID.randomUUID().toString();

        String objectKey = "posts/" + postId + "/" + uuid + "-" + originalFileName;
        String contentType = multipartFile.getContentType();

        if (contentType == null ||
                (!contentType.equals("image/jpeg") &&
                        !contentType.equals("image/png"))) {
            throw new BadRequestException("Only JPEG and PNG images are allowed");
        }

        long maxSize = 5 * 1024 * 1024;

        if (multipartFile.getSize() > maxSize) {
            throw new BadRequestException("File size exceeds 5MB");
        }

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
            throw new BadRequestException("Failed to upload file");
        }

        return new UploadedFile(
                objectKey,
                originalFileName,
                contentType,
                multipartFile.getSize()

        );
    }

    public void delete(String objectKey) {
        if(objectKey.isBlank()) {
            throw new BadRequestException("Object key is blank");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}
