package com.sporty.assignment.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SettlementMessage(
        @Positive
        long betId,
        @Positive
        long userId,
        @Positive
        long eventId,
        @Positive
        long eventMarketId,
        @Positive
        long eventWinnerId,
        @Positive
        double betAmount,
        @NotNull
        SettlementStatus status,
        @NotNull
        Instant settledAt
) {
    public String idempotencyKey() {
        return eventId + ":" + betId;
    }

    public static SettlementMessage from(Bet bet, EventOutcome outcome, SettlementStatus status) {
        return new SettlementMessage(
                bet.betId(),
                bet.userId(),
                bet.eventId(),
                bet.eventMarketId(),
                outcome.eventWinnerId(),
                bet.betAmount(),
                status,
                Instant.now()
        );
    }
}
