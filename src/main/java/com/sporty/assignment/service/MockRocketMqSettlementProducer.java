package com.sporty.assignment.service;

import com.sporty.assignment.broker.MockRocketMqBroker;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.util.JsonCodec;

import java.util.logging.Logger;

public final class MockRocketMqSettlementProducer implements SettlementProducer {
    public static final String TOPIC = "bet-settlements";
    private static final Logger LOGGER = Logger.getLogger(MockRocketMqSettlementProducer.class.getName());

    private final MockRocketMqBroker broker;

    public MockRocketMqSettlementProducer(MockRocketMqBroker broker) {
        this.broker = broker;
    }

    @Override
    public void send(SettlementMessage settlementMessage) {
        String payload = JsonCodec.toJson(settlementMessage);
        LOGGER.info(() -> "Publishing settlement: " + payload);
        broker.publish(TOPIC, payload);
    }
}

