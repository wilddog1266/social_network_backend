package com.example.post_service.controller;

import com.example.post_service.request.CreatePostRequest;
import com.example.post_service.response.PostResponse;
import com.example.post_service.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    @PostMapping("/{id}/media")
    public ResponseEntity<PostResponse> addMediaToPost(@PathVariable Long id,
                                                       @RequestParam(name = "file")MultipartFile file) {
        postService.addMediaToPost(id, file);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/me")
    public Page<PostResponse> getMyPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size) {
        return postService.getMyPosts(page, size);
    }

    @GetMapping("/user/{userId}")
    public Page<PostResponse> getPostByUserId(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size) {
        return postService.getPostsByUserId(userId, page, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostResponse> updatePostById(@PathVariable Long id,
                                                       @Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.updatePostById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostById(@PathVariable Long id) {
        postService.deletePostById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/media/{mediaId}")
    public ResponseEntity<PostResponse> deleteMediaFromPost(@PathVariable Long postId,
                                                            @PathVariable Long mediaId) {
        return ResponseEntity.ok(postService.deleteMediaFromPost(postId, mediaId));
    }
}
