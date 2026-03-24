package com.example.notification_service.config;

import com.example.common.PostCommentedEvent;
import com.example.common.PostReactionAddedEvent;
import com.example.common.UserFollowedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, UserFollowedEvent> userFollowedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<UserFollowedEvent> deserializer =
                new JsonDeserializer<>(UserFollowedEvent.class);
        deserializer.addTrustedPackages("com.example.common");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserFollowedEvent> userFollowedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserFollowedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userFollowedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PostCommentedEvent> postCommentedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<PostCommentedEvent> deserializer =
                new JsonDeserializer<>(PostCommentedEvent.class);
        deserializer.addTrustedPackages("com.example.common");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostCommentedEvent> postCommentedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PostCommentedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(postCommentedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PostReactionAddedEvent> postReactionAddedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<PostReactionAddedEvent> deserializer =
                new JsonDeserializer<>(PostReactionAddedEvent.class);
        deserializer.addTrustedPackages("com.example.common");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostReactionAddedEvent> postReactionAddedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PostReactionAddedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(postReactionAddedConsumerFactory());
        return factory;
    }
}