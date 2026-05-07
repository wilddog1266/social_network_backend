package com.example.post_service.response;

import com.example.post_service.response.enums.MediaStatus;
import com.example.post_service.response.enums.MediaType;
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
