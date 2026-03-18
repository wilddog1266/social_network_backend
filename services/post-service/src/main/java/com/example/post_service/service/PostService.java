package com.example.post_service.service;

import com.example.common.exception.NotFoundException;
import com.example.common.security.CurrentUser;
import com.example.post_service.dto.UploadedFile;
import com.example.post_service.entity.PostEntity;
import com.example.post_service.entity.PostMediaEntity;
import com.example.post_service.repository.PostMediaRepository;
import com.example.post_service.repository.PostRepository;
import com.example.post_service.request.CreatePostRequest;
import com.example.post_service.response.PostMediaResponse;
import com.example.post_service.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMediaStorageService postMediaStorageService;
    private final PostMediaRepository postMediaRepository;

    @Value("${app.minio.url}")
    private String minioUrl;

    @Value("${app.minio.bucket}")
    private String bucket;

    public PostResponse createPost(CreatePostRequest request) {
        CurrentUser currentUser = getCurrentUser();

        PostEntity postEntity = new PostEntity();
        postEntity.setContent(request.getContent().trim());
        postEntity.setAuthorId(currentUser.userId());

        PostEntity saved = postRepository.save(postEntity);

        return entityToResponse(saved);
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

    public PostResponse addMediaToPost(Long id, MultipartFile file) {
        CurrentUser currentUser = getCurrentUser();

        postRepository.findByIdAndAuthorId(id, currentUser.userId())
                .orElseThrow(() -> new NotFoundException("Post not found or access denied"));

        UploadedFile uploadedFile = postMediaStorageService.upload(id, file);

        PostMediaEntity postMediaEntity = new PostMediaEntity();
        postMediaEntity.setContentType(uploadedFile.getContentType());
        postMediaEntity.setFileName(uploadedFile.getFileName());
        postMediaEntity.setFileSize(uploadedFile.getFileSize());
        postMediaEntity.setObjectKey(uploadedFile.getObjectKey());
        postMediaEntity.setPostId(id);
        postMediaEntity.setSortOrder(0);

        postMediaRepository.save(postMediaEntity);

        return getPostById(id);
    }

    public PostResponse deleteMediaFromPost(Long id, Long mediaId) {
        CurrentUser currentUser = getCurrentUser();

        postRepository.findByIdAndAuthorId(id, currentUser.userId())
                .orElseThrow(() -> new NotFoundException("Post not found or access denied"));

        PostMediaEntity postMediaEntity = postMediaRepository.findByIdAndPostId(mediaId, id)
                .orElseThrow(() -> new NotFoundException("Media not found for this post"));

        postMediaStorageService.delete(postMediaEntity.getObjectKey());
        postMediaRepository.delete(postMediaEntity);

        return getPostById(id);
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
        String url = minioUrl + "/" + bucket + "/" + entity.getObjectKey();
        return new PostMediaResponse(entity.getId(), url, entity.getFileName(), entity.getContentType());
    }

    private PostResponse entityToResponse(PostEntity entity) {
        List<PostMediaResponse> media = mediaEntitiesToResponses(entity.getId());

        return new PostResponse(entity.getId(), entity.getAuthorId(), entity.getContent(), entity.getCreatedAt(), entity.getUpdatedAt(), media);
    }
}
