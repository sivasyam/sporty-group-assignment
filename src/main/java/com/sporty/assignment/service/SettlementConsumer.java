package com.sporty.assignment.service;

import com.sporty.assignment.broker.MockRocketMqBroker;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.util.JsonCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public final class SettlementConsumer {
    private static final Logger LOGGER = Logger.getLogger(SettlementConsumer.class.getName());

    private final MockRocketMqBroker broker;
    private final List<SettlementMessage> received = new ArrayList<>();
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
            synchronized (received) {
                received.add(message);
            }
            LOGGER.info(() -> "Consumed settlement: " + payload);
        });
    }

    public List<SettlementMessage> received() {
        synchronized (received) {
            return List.copyOf(received);
        }
    }
}

