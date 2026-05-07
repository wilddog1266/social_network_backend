package com.example.user_service.response.media;

import com.example.user_service.response.media.enums.MediaStatus;
import com.example.user_service.response.media.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MediaClientResponse {

    private Long id;

    private String objectKey;

    private String bucket;

    private Long size;

    private String originalFileName;

    private String url;

    private MediaStatus status;

    private MediaType type;
}