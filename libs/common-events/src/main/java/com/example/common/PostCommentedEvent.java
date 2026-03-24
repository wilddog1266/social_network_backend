package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentedEvent {

    private Long postId;

    private Long postAuthorId;

    private Long commentId;

    private Long commentAuthorId;
}
