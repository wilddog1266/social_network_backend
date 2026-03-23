package com.example.reaction_service.controller;

import com.example.reaction_service.response.PostReactionSummaryResponse;
import com.example.reaction_service.service.PostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reactions/posts")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostReactionService postReactionService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostReactionSummaryResponse> like(@PathVariable Long postId) {
        return ResponseEntity.ok(postReactionService.like(postId));
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<PostReactionSummaryResponse> dislike(@PathVariable Long postId) {
        return ResponseEntity.ok(postReactionService.dislike(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostReactionSummaryResponse> remove(@PathVariable Long postId) {
        return ResponseEntity.ok(postReactionService.removeReaction(postId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostReactionSummaryResponse> getSummary(@PathVariable Long postId) {
        return ResponseEntity.ok(postReactionService.getSummary(postId));
    }
}
