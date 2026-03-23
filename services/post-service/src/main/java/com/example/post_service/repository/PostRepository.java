package com.example.post_service.repository;

import com.example.post_service.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    Optional<PostEntity> findByIdAndAuthorId(Long id, Long authorId);
    Page<PostEntity> findByAuthorIdIn(List<Long> authorIds, Pageable pageable);
}
