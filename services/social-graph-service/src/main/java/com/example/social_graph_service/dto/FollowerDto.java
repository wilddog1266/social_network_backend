package com.example.social_graph_service.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class FollowerDto {

    private Long userId;

    private Instant createdAt;

}
