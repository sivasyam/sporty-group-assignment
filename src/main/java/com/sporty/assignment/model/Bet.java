package com.sporty.assignment.model;

public record Bet(
        Long betId,
        Long userId,
        Long eventId,
        Long eventMarketId,
        Long eventWinnerId,
        Double betAmount
) {
}

