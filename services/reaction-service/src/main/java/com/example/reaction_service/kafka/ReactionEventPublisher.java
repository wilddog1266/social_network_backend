package com.example.reaction_service.kafka;

import com.example.common.PostReactionAddedEvent;
import com.example.common.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionEventPublisher {

    private final KafkaTemplate<String, PostReactionAddedEvent> kafkaTemplate;

    @Value("${app.kafka.topic.post-reaction-added}")
    private String topic;

    public void publishReactionEvent(Long postId, Long postAuthorId, Long reactionAuthorId, ReactionType reactionType) {
        PostReactionAddedEvent event = new PostReactionAddedEvent(
                postId, postAuthorId, reactionAuthorId, reactionType
        );

        String key = String.valueOf(postAuthorId);
        kafkaTemplate.send(topic, key, event);
    }

}

