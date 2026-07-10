package com.sporty.assignment.service;

import com.sporty.assignment.broker.MockKafkaBroker;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.util.JsonCodec;

public final class EventOutcomePublisher {
    public static final String TOPIC = "event-outcomes";

    private final MockKafkaBroker broker;

    public EventOutcomePublisher(MockKafkaBroker broker) {
        this.broker = broker;
    }

    public void publish(EventOutcome outcome) {
        broker.publish(TOPIC, JsonCodec.toJson(outcome));
    }
}

