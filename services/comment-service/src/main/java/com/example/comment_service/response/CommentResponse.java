package com.example.comment_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;

    private Long postId;

    private Long authorId;

    private String content;

    private Instant createdAt;

    private Instant updatedAt;
}
