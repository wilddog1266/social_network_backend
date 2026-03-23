package com.example.feed_service.controller;

import com.example.feed_service.response.FeedPostResponse;
import com.example.feed_service.response.PageResponse;
import com.example.feed_service.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public PageResponse<FeedPostResponse> getFeed(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return feedService.getFeed(page, size);
    }
}