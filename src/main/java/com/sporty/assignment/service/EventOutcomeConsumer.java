package com.sporty.assignment.service;

import com.sporty.assignment.kafka.KafkaTopics;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.util.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public final class EventOutcomeConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventOutcomeConsumer.class);
    private final BetSettlementService settlementService;

    public EventOutcomeConsumer(BetSettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @KafkaListener(topics = KafkaTopics.EVENT_OUTCOMES, groupId = "sporty-group-assignment")
    public void onMessage(String payload) {
        EventOutcome outcome = JsonCodec.fromEventOutcome(payload);
        LOGGER.info("event=received_event_outcome event_id={}",
                outcome.eventId());
        settlementService.settle(outcome);
    }
}
