package com.example.comment_service.service;

import com.example.comment_service.client.PostClient;
import com.example.comment_service.entity.CommentEntity;
import com.example.comment_service.kafka.CommentEventPublisher;
import com.example.comment_service.repository.CommentRepository;
import com.example.comment_service.request.CreateCommentRequest;
import com.example.comment_service.request.UpdateCommentRequest;
import com.example.comment_service.response.CommentResponse;
import com.example.comment_service.response.PostAuthorResponse;
import com.example.common.exception.BadRequestException;
import com.example.common.exception.NotFoundException;
import com.example.common.security.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostClient postClient;
    private final HttpServletRequest httpServletRequest;
    private final CommentEventPublisher commentEventPublisher;

    public CommentResponse createComment(CreateCommentRequest request) {
        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }

        CurrentUser currentUser = getCurrentUser();

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setAuthorId(currentUser.userId());
        commentEntity.setContent(request.getContent().trim());
        commentEntity.setPostId(request.getPostId());

        CommentEntity saved = commentRepository.save(commentEntity);

        String authHeader = httpServletRequest.getHeader("Authorization");
        PostAuthorResponse post = postClient.getPostById(saved.getPostId(), authHeader);

        commentEventPublisher.publishPostCommented(saved.getPostId(), post.getAuthorId(), saved.getId(), currentUser.userId());

        return entityToResponse(saved);
    }

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(this::entityToResponse)
                .toList();
    }

    public CommentResponse updateComment(Long id, UpdateCommentRequest request) {
        CurrentUser currentUser = getCurrentUser();

        CommentEntity entity = commentRepository.findByIdAndAuthorId(id, currentUser.userId())
                .orElseThrow(() -> new NotFoundException("Comment not found or access denied"));

        entity.setContent(request.getContent().trim());

        CommentEntity saved = commentRepository.save(entity);

        return entityToResponse(saved);
    }

    public void deleteCommentById(Long id) {
        CurrentUser currentUser = getCurrentUser();

        CommentEntity entity = commentRepository.findByIdAndAuthorId(id, currentUser.userId())
                .orElseThrow(() -> new NotFoundException("Comment not found or access denied"));

        commentRepository.delete(entity);
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private CommentResponse entityToResponse(CommentEntity entity) {
        return new CommentResponse(entity.getId(), entity.getPostId(), entity.getAuthorId(), entity.getContent(), entity.getCreatedAt(), entity.getUpdatedAt());

    }
}
