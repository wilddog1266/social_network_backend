package com.example.post_service.repository;

import com.example.post_service.entity.PostMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMediaEntity, Long> {

    List<PostMediaEntity> findByPostIdOrderBySortOrderAsc(Long postId);

    Optional<PostMediaEntity> findByIdAndPostId(Long id, Long postId);
}
