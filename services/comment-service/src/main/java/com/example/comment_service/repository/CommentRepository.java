package com.example.comment_service.repository;

import com.example.comment_service.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    List<CommentEntity> findByAuthorId(Long authorId);

    Optional<CommentEntity> findByIdAndAuthorId(Long id, Long authorId);
}
