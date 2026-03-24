package com.example.reaction_service.client;

import com.example.reaction_service.response.PostAuthorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PostClient {

    private final RestClient postRestClient;

    public PostAuthorResponse getPostById(Long postId, String bearerToken) {
        return postRestClient
                .get()
                .uri("/api/posts/{id}", postId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .body(PostAuthorResponse.class);
    }
}