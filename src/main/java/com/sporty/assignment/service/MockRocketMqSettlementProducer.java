package com.sporty.assignment.service;

import com.sporty.assignment.broker.MockRocketMqBroker;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.util.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MockRocketMqSettlementProducer implements SettlementProducer {
    public static final String TOPIC = "bet-settlements";
    private static final Logger LOGGER = LoggerFactory.getLogger(MockRocketMqSettlementProducer.class);

    private final MockRocketMqBroker broker;

    public MockRocketMqSettlementProducer(MockRocketMqBroker broker) {
        this.broker = broker;
    }

    @Override
    public void send(SettlementMessage settlementMessage) {
        String payload = JsonCodec.toJson(settlementMessage);
        LOGGER.info(
                "event=publish_settlement topic={} bet_id={} event_id={} status={}",
                TOPIC,
                settlementMessage.betId(),
                settlementMessage.eventId(),
                settlementMessage.status()
        );
        broker.publish(TOPIC, payload);
    }
}
