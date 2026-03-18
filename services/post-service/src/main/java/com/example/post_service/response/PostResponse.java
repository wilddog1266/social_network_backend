package com.example.post_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {

    private Long id;

    private Long authorId;

    private String content;

    private Instant createdAt;

    private Instant updatedAt;

    private List<PostMediaResponse> media;
}
