package com.example.post_service.service;

import com.example.common.exception.BadRequestException;
import com.example.common.exception.NotFoundException;
import com.example.common.security.CurrentUser;
import com.example.post_service.client.MediaClient;
import com.example.post_service.entity.PostEntity;
import com.example.post_service.entity.PostMediaEntity;
import com.example.post_service.repository.PostMediaRepository;
import com.example.post_service.repository.PostRepository;
import com.example.post_service.request.CreatePostRequest;
import com.example.post_service.response.MediaClientResponse;
import com.example.post_service.response.PostMediaResponse;
import com.example.post_service.response.PostResponse;
import com.example.post_service.response.enums.MediaStatus;
import com.example.post_service.response.enums.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final MediaClient mediaClient;

    public PostResponse createPost(CreatePostRequest request) {
        CurrentUser currentUser = getCurrentUser();

        PostEntity postEntity = new PostEntity();
        postEntity.setContent(request.getContent().trim());
        postEntity.setAuthorId(currentUser.userId());

        PostEntity saved = postRepository.save(postEntity);

        return entityToResponse(saved);
    }

    public PostResponse attachMediaToPost(Long postId, Long mediaId) {
        Long currentUserId = getCurrentUser().userId();

        PostEntity currentPost = postRepository.findByIdAndAuthorId(postId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Post not found or access denied"));

        MediaClientResponse media = mediaClient.getMediaById(mediaId);

        if(!media.getStatus().equals(MediaStatus.READY) || !media.getType().equals(MediaType.POST_IMAGE)) {
            throw new BadRequestException("Can't attach media");
        }

        if(postMediaRepository.existsByPostIdAndMediaId(postId, mediaId)) {
            throw new BadRequestException("Media already attached to post");
        }

        PostMediaEntity saved = new PostMediaEntity();
        saved.setSortOrder(postMediaRepository.findByPostIdOrderBySortOrderAsc(postId).size());
        saved.setMediaId(mediaId);
        saved.setPostId(postId);

        postMediaRepository.save(saved);

        return entityToResponse(currentPost);

    }

    public PostResponse getPostById(Long id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post with id '" + id + "' not found"));

        return entityToResponse(postEntity);
    }

    public Page<PostResponse> getMyPosts(int page, int size) {
        CurrentUser currentUser = getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<PostEntity> postEntityPage = postRepository.findByAuthorIdOrderByCreatedAtDesc(currentUser.userId(), pageRequest);

        return postEntityPage.map(this::entityToResponse);
    }

    public Page<PostResponse> getPostsByUserId(Long userId ,int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<PostEntity> postEntityPage = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageRequest);

        return postEntityPage.map(this::entityToResponse);
    }

    public PostResponse updatePostById(Long id, CreatePostRequest request) {
        CurrentUser currentUser = getCurrentUser();

        PostEntity postEntity = postRepository.findByIdAndAuthorId(id, currentUser.userId())
                .orElseThrow(() -> new NotFoundException("Post not found or access denied"));

        postEntity.setContent(request.getContent().trim());
        PostEntity saved = postRepository.save(postEntity);

        return entityToResponse(saved);
    }

    public PostResponse deleteMediaFromPost(Long postId, Long mediaId) {
        Long currentUserId = getCurrentUser().userId();

        PostEntity postEntity = postRepository.findByIdAndAuthorId(postId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Post not found or access denied"));

        PostMediaEntity mediaEntity = postMediaRepository.findByPostIdAndMediaId(postId, mediaId)
                .orElseThrow(() -> new NotFoundException("Media not found or access denied"));

        postMediaRepository.delete(mediaEntity);

        return entityToResponse(postEntity);
    }

    public Page<PostResponse> findPostsByAuthorIds(List<Long> authorIds, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PostEntity> postEntityPage = postRepository.findByAuthorIdIn(authorIds, pageRequest);

        return postEntityPage.map(this::entityToResponse);
    }

    public void deletePostById(Long id) {
        CurrentUser currentUser = getCurrentUser();

        PostEntity postEntity = postRepository.findByIdAndAuthorId(id, currentUser.userId())
                .orElseThrow(() -> new NotFoundException("Post not found or access denied"));

        postRepository.delete(postEntity);
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private List<PostMediaResponse> mediaEntitiesToResponses(Long id) {
        return postMediaRepository.findByPostIdOrderBySortOrderAsc(id).stream()
                .map(this::mediaEntityToResponse)
                .toList();
    }

    private PostMediaResponse mediaEntityToResponse(PostMediaEntity entity) {
        return new PostMediaResponse(entity.getMediaId());
    }

    private PostResponse entityToResponse(PostEntity entity) {
        List<PostMediaResponse> media = mediaEntitiesToResponses(entity.getId());

        return new PostResponse(entity.getId(), entity.getAuthorId(), entity.getContent(), entity.getCreatedAt(), entity.getUpdatedAt(), media);
    }
}
