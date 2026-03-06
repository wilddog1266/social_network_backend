package com.example.social_graph_service.controller;

import com.example.social_graph_service.dto.FollowerDto;
import com.example.social_graph_service.dto.FollowingDto;
import com.example.social_graph_service.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long userId
    ) {

        boolean created = followService.follow(currentUserId, userId);

        return created ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollow(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long userId
    ) {
        followService.unfollow(currentUserId, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/following")
    public Page<FollowingDto> getFollowing(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return followService.getFollowing(currentUserId, pageable);
    }

    @GetMapping("/followers")
    public Page<FollowerDto> getFollowers(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return followService.getFollowers(currentUserId, pageable);
    }
}
