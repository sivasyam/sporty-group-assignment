package com.sporty.assignment.service;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.model.SettlementStatus;
import com.sporty.assignment.repository.InMemoryBetRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BetSettlementServiceTest {
    @Test
    void settlesOnlyMatchingBetsAndPublishesThem() {
        InMemoryBetRepository repository = new InMemoryBetRepository();
        repository.save(new Bet(1L, 10L, 100L, 5L, 2L, 25.0));
        repository.save(new Bet(2L, 11L, 100L, 6L, 3L, 40.0));
        repository.save(new Bet(3L, 12L, 200L, 7L, 4L, 60.0));

        CapturingSettlementProducer producer = new CapturingSettlementProducer();
        BetSettlementService service = new BetSettlementService(repository, producer);

        List<SettlementMessage> settlements = service.settle(new EventOutcome(100L, "Match 100", 2L));

        assertThat(settlements).hasSize(2);
        assertThat(settlements).extracting(SettlementMessage::betId).containsExactly(1L, 2L);
        assertThat(settlements).extracting(SettlementMessage::status).containsExactly(
                SettlementStatus.WON,
                SettlementStatus.LOST
        );
        assertThat(producer.sent).hasSize(2);
        assertThat(producer.sent).extracting(SettlementMessage::betId).containsExactly(1L, 2L);
    }

    private static final class CapturingSettlementProducer implements SettlementProducer {
        private final List<SettlementMessage> sent = new ArrayList<>();

        @Override
        public void send(SettlementMessage settlementMessage) {
            sent.add(settlementMessage);
        }
    }
}
