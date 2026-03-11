package com.example.user_service.repository;

import com.example.user_service.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    Optional<UserProfileEntity> findByUserId(Long userId);

    Optional<UserProfileEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUserId(Long userId);
}
