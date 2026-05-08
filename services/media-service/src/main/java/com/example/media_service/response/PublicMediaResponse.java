package com.example.media_service.response;

import com.example.media_service.entity.enums.MediaStatus;
import com.example.media_service.entity.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

//TODO: consider if we need to return objectKey and bucket to the client. If don't -> return this Response
public class PublicMediaResponse {

    private Long id;

    private String url;

    private MediaType type;

    private MediaStatus status;

    private String originalFileName;

    private Long size;
}
