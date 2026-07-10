package com.sporty.assignment.model;

import java.time.Instant;

public record SettlementMessage(
        long betId,
        long userId,
        long eventId,
        long eventMarketId,
        long eventWinnerId,
        double betAmount,
        SettlementStatus status,
        Instant settledAt
) {
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

