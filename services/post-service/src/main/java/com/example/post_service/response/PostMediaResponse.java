package com.example.post_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMediaResponse {
    private Long id;
    private String url;
    private String fileName;
    private String contentType;
}
