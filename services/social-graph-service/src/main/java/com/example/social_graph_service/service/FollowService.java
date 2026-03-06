package com.example.social_graph_service.service;

import com.example.social_graph_service.dto.FollowerDto;
import com.example.social_graph_service.dto.FollowingDto;
import com.example.social_graph_service.entity.FollowEntity;
import com.example.social_graph_service.entity.FollowId;
import com.example.social_graph_service.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public boolean follow(Long currentUserId, Long targetUserId) {
        if(Objects.equals(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("User can't subscribe to himself");
        }

        if(followRepository.existsByIdFollowerIdAndIdFolloweeId(currentUserId, targetUserId)) {
            return false;
        }

        FollowId id = new FollowId(currentUserId, targetUserId);
        FollowEntity followEntity = new FollowEntity(id);

        followRepository.save(followEntity);
        return true;
    }

    public void unfollow(Long currentUserId, Long targetUserId) {
        if(Objects.equals(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("User can't unsubscribe to himself");
        }

        FollowId id = new FollowId(currentUserId, targetUserId);

        followRepository.deleteById(id);
    }

    public Page<FollowingDto> getFollowing(Long currentUserId, Pageable pageable) {
        Page<FollowEntity> page = followRepository.findByIdFollowerId(currentUserId, pageable);

        return page.map(followEntity -> {
            FollowingDto dto = new FollowingDto();
            dto.setUserId(followEntity.getId().getFolloweeId());
            dto.setCreatedAt(followEntity.getCreatedAt());
            return dto;
        });
    }

    public Page<FollowerDto> getFollowers(Long currentUserId, Pageable pageable) {
        Page<FollowEntity> page = followRepository.findByIdFolloweeId(currentUserId, pageable);

        return page.map(followEntity -> {
            FollowerDto dto = new FollowerDto();
            dto.setUserId(followEntity.getId().getFollowerId());
            dto.setCreatedAt(followEntity.getCreatedAt());
            return dto;
        });
    }
}
