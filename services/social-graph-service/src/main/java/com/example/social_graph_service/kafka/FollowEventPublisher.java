package com.example.social_graph_service.kafka;

import com.example.common.UserFollowedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FollowEventPublisher  {

    private final KafkaTemplate<String, UserFollowedEvent> template;

    @Value("${app.kafka.topic.user-followed}")
    private String topic;

    public void publishUserFollowed(Long followerId, Long followeeId) {
        UserFollowedEvent userFollowedEvent = new UserFollowedEvent(followerId, followeeId, Instant.now());
        String key = String.valueOf(followeeId);
        template.send(topic, key, userFollowedEvent);
    }
}
