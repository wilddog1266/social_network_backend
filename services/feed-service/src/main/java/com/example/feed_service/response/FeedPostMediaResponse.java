package com.example.feed_service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedPostMediaResponse {

    private Long id;

    private String url;

    private String fileName;
}
