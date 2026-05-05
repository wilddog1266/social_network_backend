package com.example.media_service.response;

import com.example.media_service.entity.enums.MediaStatus;
import com.example.media_service.entity.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MediaResponse {

    private Long id;

    private String objectKey;

    private String bucket;

    private Long size;

    private String originalFileName;

    private String url;

    private MediaStatus status;

    private MediaType type;
}
