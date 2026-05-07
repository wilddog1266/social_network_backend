package com.example.post_service.controller;

import com.example.post_service.request.CreatePostRequest;
import com.example.post_service.response.PostResponse;
import com.example.post_service.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Operations for posts and post media management")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create a new post")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    @PostMapping(value = "/{postId}/media/{mediaId}")
    @Operation(summary = "Attach media file to post")
    public ResponseEntity<PostResponse> addMediaToPost(@PathVariable Long postId,
                                                       @PathVariable Long mediaId) {

        return ResponseEntity.ok(postService.attachMediaToPost(postId, mediaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by id")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's posts")
    public Page<PostResponse> getMyPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size) {
        return postService.getMyPosts(page, size);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get posts by user id")
    public Page<PostResponse> getPostByUserId(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size) {
        return postService.getPostsByUserId(userId, page, size);
    }

    @GetMapping("/by-authors")
    @Operation(summary = "Get posts by author ids")
    public Page<PostResponse> getPostsByAuthorIds(@RequestParam List<Long> authorIds,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return postService.findPostsByAuthorIds(authorIds, page, size);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update post by id")
    public ResponseEntity<PostResponse> updatePostById(@PathVariable Long id,
                                                       @Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.updatePostById(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post by id")
    public ResponseEntity<Void> deletePostById(@PathVariable Long id) {
        postService.deletePostById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/media/{mediaId}")
    @Operation(summary = "Delete media from post")
    public ResponseEntity<PostResponse> deleteMediaFromPost(@PathVariable Long postId,
                                                            @PathVariable Long mediaId) {
        return ResponseEntity.ok(postService.deleteMediaFromPost(postId, mediaId));
    }
}
