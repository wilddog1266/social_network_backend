package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostReactionAddedEvent {

    private Long postId;

    private Long postAuthorId;

    private Long reactionAuthorId;

    private ReactionType reactionType;
}
