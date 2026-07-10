package com.sporty.assignment.util;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.model.SettlementStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonCodecTest {
    @Test
    void roundTripsEventOutcome() {
        EventOutcome outcome = new EventOutcome(10L, "Final", 3L);

        String json = JsonCodec.toJson(outcome);
        EventOutcome parsed = JsonCodec.fromEventOutcome(json);

        assertThat(parsed).isEqualTo(outcome);
    }

    @Test
    void roundTripsBet() {
        Bet bet = new Bet(1L, 7L, 10L, 4L, 3L, 15.5);

        String json = JsonCodec.toJson(bet);
        Bet parsed = JsonCodec.fromBet(json);

        assertThat(parsed).isEqualTo(bet);
    }

    @Test
    void roundTripsSettlementMessage() {
        SettlementMessage message = new SettlementMessage(
                1, 7, 10, 4, 3, 15.5, SettlementStatus.WON, java.time.Instant.parse("2026-07-10T22:00:00Z")
        );

        String json = JsonCodec.toJson(message);
        SettlementMessage parsed = JsonCodec.fromSettlementMessage(json);

        assertThat(parsed).isEqualTo(message);
    }

    @Test
    void rejectsMissingFields() {
        assertThatThrownBy(() -> JsonCodec.fromEventOutcome("{\"eventId\":1}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing field");
    }
}

