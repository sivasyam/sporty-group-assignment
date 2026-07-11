package com.sporty.assignment.service;

import com.sporty.assignment.broker.MockRocketMqBroker;
import com.sporty.assignment.model.Bet;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.model.SettlementStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SettlementConsumerTest {
    @Test
    void ignoresDuplicateSettlementMessages() {
        MockRocketMqBroker broker = new MockRocketMqBroker();
        SettlementConsumer consumer = new SettlementConsumer(broker);
        consumer.start();

        SettlementMessage settlement = SettlementMessage.from(
                new Bet(1L, 10L, 100L, 5L, 2L, 25.0),
                new EventOutcome(100L, "Match 100", 2L),
                SettlementStatus.WON
        );
        String payload = com.sporty.assignment.util.JsonCodec.toJson(settlement);

        broker.publish(MockRocketMqSettlementProducer.TOPIC, payload);
        broker.publish(MockRocketMqSettlementProducer.TOPIC, payload);

        assertThat(consumer.received()).hasSize(1);
        assertThat(consumer.received().getFirst().idempotencyKey()).isEqualTo("100:1");
    }
}
