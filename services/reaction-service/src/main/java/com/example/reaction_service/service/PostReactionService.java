package com.example.reaction_service.service;

import com.example.common.security.CurrentUser;
import com.example.reaction_service.entity.PostReactionEntity;
import com.example.reaction_service.entity.ReactionType;
import com.example.reaction_service.repository.PostReactionRepository;
import com.example.reaction_service.response.PostReactionSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReactionService {

    private final PostReactionRepository postReactionRepository;

    public PostReactionSummaryResponse like(Long postId) {
        CurrentUser currentUser = getCurrentUser();

        Optional<PostReactionEntity> existing = postReactionRepository.findByPostIdAndUserId(postId, currentUser.userId());

        if(existing.isEmpty()) {
            PostReactionEntity reaction = new PostReactionEntity();
            reaction.setReactionType(ReactionType.LIKE);
            reaction.setPostId(postId);
            reaction.setUserId(currentUser.userId());
            postReactionRepository.save(reaction);
        } else {
            PostReactionEntity current = existing.get();

            if (current.getReactionType() == ReactionType.DISLIKE) {
                current.setReactionType(ReactionType.LIKE);
                postReactionRepository.save(current);
            }
        }

        return buildSummary(postId, currentUser.userId());
    }

    public PostReactionSummaryResponse dislike(Long postId) {
        CurrentUser currentUser = getCurrentUser();

        Optional<PostReactionEntity> existing = postReactionRepository.findByPostIdAndUserId(postId, currentUser.userId());

        if(existing.isEmpty()) {
            PostReactionEntity reaction = new PostReactionEntity();
            reaction.setReactionType(ReactionType.DISLIKE);
            reaction.setPostId(postId);
            reaction.setUserId(currentUser.userId());
            postReactionRepository.save(reaction);
        } else {
            PostReactionEntity current = existing.get();

            if (current.getReactionType() == ReactionType.LIKE) {
                current.setReactionType(ReactionType.DISLIKE);
                postReactionRepository.save(current);
            }
        }

        return buildSummary(postId, currentUser.userId());
    }

    public PostReactionSummaryResponse removeReaction(Long postId) {
        CurrentUser currentUser = getCurrentUser();

        Optional<PostReactionEntity> existing =
                postReactionRepository.findByPostIdAndUserId(postId, currentUser.userId());

        existing.ifPresent(postReactionRepository::delete);

        return buildSummary(postId, currentUser.userId());
    }

    public PostReactionSummaryResponse getSummary(Long postId) {
        CurrentUser currentUser = getCurrentUser();

        return buildSummary(postId, currentUser.userId());
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private PostReactionSummaryResponse buildSummary(Long postId, Long userId) {
        long like = postReactionRepository.countByPostIdAndReactionType(postId, ReactionType.LIKE);
        long dislike = postReactionRepository.countByPostIdAndReactionType(postId, ReactionType.DISLIKE);
        ReactionType currentUserReaction = postReactionRepository.findByPostIdAndUserId(postId, userId)
                .map(r -> r.getReactionType())
                .orElse(null);

        PostReactionSummaryResponse response = new PostReactionSummaryResponse();
        response.setMyReaction(currentUserReaction);
        response.setLikeCount(like);
        response.setDislikeCount(dislike);
        response.setPostId(postId);

        return response;
    }
}
