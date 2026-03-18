package com.example.post_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFile {

    private String objectKey;

    private String fileName;

    private String contentType;

    private Long fileSize;

}
