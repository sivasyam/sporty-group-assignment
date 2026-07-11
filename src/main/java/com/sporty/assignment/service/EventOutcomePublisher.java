package com.sporty.assignment.service;

import com.sporty.assignment.kafka.KafkaTopics;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.util.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public final class EventOutcomePublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventOutcomePublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public EventOutcomePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(EventOutcome outcome) {
        LOGGER.info("event=publish_event_outcome event_id={} topic={}",
                outcome.eventId(), KafkaTopics.EVENT_OUTCOMES);
        kafkaTemplate.send(KafkaTopics.EVENT_OUTCOMES, JsonCodec.toJson(outcome));
    }
}
