package com.example.feed_service.client;

import com.example.feed_service.response.FeedPostResponse;
import com.example.feed_service.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostClient {

    private final RestClient postRestClient;


    public PageResponse<FeedPostResponse> getPostsByAuthorIds(
            List<Long> ids,
            int page,
            int size,
            String bearerToken
    ) {
        String idsParam = ids.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        return postRestClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/api/posts/by-authors")
                            .queryParam("page", page)
                            .queryParam("size", size);

                    for (Long id : ids) {
                        builder.queryParam("authorIds", id);
                    }

                    return builder.build();
                })
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponse<FeedPostResponse>>() {});
    }
}
