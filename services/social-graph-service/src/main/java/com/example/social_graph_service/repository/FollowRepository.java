package com.example.social_graph_service.repository;

import com.example.social_graph_service.entity.FollowEntity;
import com.example.social_graph_service.entity.FollowId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, FollowId> {

    boolean existsByIdFollowerIdAndIdFolloweeId(Long followerId, Long followeeId);

    Page<FollowEntity> findByIdFollowerId(Long followerId, Pageable pageable);

    Page<FollowEntity> findByIdFolloweeId(Long followeeId, Pageable pageable);

}
