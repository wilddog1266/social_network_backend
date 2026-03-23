package com.example.feed_service.client;

import com.example.feed_service.response.FollowingResponse;
import com.example.feed_service.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialGraphClient {

    private final RestClient socialGraphRestClient;

    public PageResponse<FollowingResponse> getFollowing(String bearerToken) {
        return socialGraphRestClient.get()
                .uri("/api/follow/following")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponse<FollowingResponse>>() {});
    }
}
