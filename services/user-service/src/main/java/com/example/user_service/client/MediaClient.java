package com.example.user_service.client;

import com.example.user_service.response.media.MediaClientResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class MediaClient {

    private final RestClient mediaRestClient;

    @Value("${clients.media-service.url}")
    private String mediaUrl;

    public MediaClientResponse getMediaById(Long mediaId) {
        return mediaRestClient.get()
                .uri(mediaUrl + "/api/media/{id}", mediaId)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .retrieve()
                .body(MediaClientResponse.class);
    }

    private String getBearerToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) {
            throw new AccessDeniedException("Attributes is empty");
        }

        HttpServletRequest request = attributes.getRequest();

        String bearer = request.getHeader("Authorization");

        if(bearer == null) {
            throw new AccessDeniedException("Access denied to media");
        }

        return bearer;
    }
}
