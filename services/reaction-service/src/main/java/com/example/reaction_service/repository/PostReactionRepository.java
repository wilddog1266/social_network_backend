package com.example.reaction_service.repository;

import com.example.reaction_service.entity.PostReactionEntity;
import com.example.reaction_service.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReactionEntity, Long> {

    Optional<PostReactionEntity> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostIdAndReactionType(Long postId, ReactionType reactionType);
}
