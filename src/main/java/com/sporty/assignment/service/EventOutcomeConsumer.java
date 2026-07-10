package com.sporty.assignment.service;

import com.sporty.assignment.broker.MockKafkaBroker;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.util.JsonCodec;

import java.util.concurrent.atomic.AtomicBoolean;

public final class EventOutcomeConsumer {
    private final MockKafkaBroker broker;
    private final BetSettlementService settlementService;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public EventOutcomeConsumer(MockKafkaBroker broker, BetSettlementService settlementService) {
        this.broker = broker;
        this.settlementService = settlementService;
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        broker.subscribe(EventOutcomePublisher.TOPIC, payload -> {
            EventOutcome outcome = JsonCodec.fromEventOutcome(payload);
            settlementService.settle(outcome);
        });
    }
}

