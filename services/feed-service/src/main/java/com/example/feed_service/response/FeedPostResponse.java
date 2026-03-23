package com.example.feed_service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FeedPostResponse {

    private Long id;

    private Long authorId;

    private String content;

    private Instant createdAt;

    private Instant updatedAt;

    private List<FeedPostMediaResponse> media;
}
