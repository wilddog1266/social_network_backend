package com.example.feed_service.service;

import com.example.common.security.CurrentUser;
import com.example.feed_service.client.PostClient;
import com.example.feed_service.client.SocialGraphClient;
import com.example.feed_service.response.FeedPostResponse;
import com.example.feed_service.response.FollowingResponse;
import com.example.feed_service.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final SocialGraphClient socialGraphClient;
    private final PostClient postClient;
    private final HttpServletRequest httpServletRequest;

    public PageResponse<FeedPostResponse> getFeed(int page, int size) {
        CurrentUser currentUser = getCurrentUser();

        String authHeader = httpServletRequest.getHeader("Authorization");

        PageResponse<FollowingResponse> followingResponses = socialGraphClient.getFollowing(authHeader);

        List<Long> authorIds = followingResponses.getContent().stream()
                .map(FollowingResponse::getUserId)
                .toList();

        authorIds = new ArrayList<>(authorIds);
        authorIds.add(currentUser.userId());

        return postClient.getPostsByAuthorIds(authorIds, page, size, authHeader);
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}