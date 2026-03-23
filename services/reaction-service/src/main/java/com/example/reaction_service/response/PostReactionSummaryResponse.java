package com.example.reaction_service.response;

import com.example.reaction_service.entity.ReactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostReactionSummaryResponse {

    private Long postId;

    private long likeCount;

    private long dislikeCount;

    private ReactionType myReaction;
}
