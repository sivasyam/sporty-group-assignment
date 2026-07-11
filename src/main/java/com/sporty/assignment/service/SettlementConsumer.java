package com.sporty.assignment.service;

import com.sporty.assignment.broker.MockRocketMqBroker;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.util.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SettlementConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettlementConsumer.class);

    private final MockRocketMqBroker broker;
    private final List<SettlementMessage> received = new ArrayList<>();
    private final Set<String> processedKeys = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean started = new AtomicBoolean(false);

    public SettlementConsumer(MockRocketMqBroker broker) {
        this.broker = broker;
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        broker.subscribe(MockRocketMqSettlementProducer.TOPIC, payload -> {
            SettlementMessage message = JsonCodec.fromSettlementMessage(payload);
            String idempotencyKey = message.idempotencyKey();
            if (!processedKeys.add(idempotencyKey)) {
                LOGGER.info(
                        "event=duplicate_settlement_ignored topic={} bet_id={} event_id={} idempotency_key={}",
                        MockRocketMqSettlementProducer.TOPIC,
                        message.betId(),
                        message.eventId(),
                        idempotencyKey
                );
                return;
            }
            synchronized (received) {
                received.add(message);
            }
            LOGGER.info(
                    "event=received_settlement topic={} bet_id={} event_id={} winner_id={} status={}",
                    MockRocketMqSettlementProducer.TOPIC,
                    message.betId(),
                    message.eventId(),
                    message.eventWinnerId(),
                    message.status()
            );
        });
    }

    public List<SettlementMessage> received() {
        synchronized (received) {
            return List.copyOf(received);
        }
    }
}
