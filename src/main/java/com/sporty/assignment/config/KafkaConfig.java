package com.sporty.assignment.config;

import com.sporty.assignment.kafka.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic eventOutcomesTopic() {
        return TopicBuilder.name(KafkaTopics.EVENT_OUTCOMES)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

