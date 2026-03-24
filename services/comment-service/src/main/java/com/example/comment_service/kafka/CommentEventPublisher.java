package com.example.comment_service.kafka;

import com.example.comment_service.client.PostClient;
import com.example.common.PostCommentedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final KafkaTemplate<String, PostCommentedEvent> template;

    @Value("${app.kafka.topic.post-commented}")
    private String topic;

    public void publishPostCommented(Long postId, Long postAuthorId, Long commentId, Long commentAuthorId) {
        PostCommentedEvent postCommentedEvent = new PostCommentedEvent(postId, postAuthorId, commentId, commentAuthorId);
        String key = String.valueOf(postAuthorId);
        template.send(topic, key, postCommentedEvent);
    }
}
