package com.sporty.assignment.api.request;

import com.sporty.assignment.model.Bet;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record BetRequest(
        @NotNull @Positive
        Long betId,
        @NotNull @Positive
        Long userId,
        @NotNull @Positive
        Long eventId,
        @NotNull @Positive
        Long eventMarketId,
        @NotNull @Positive
        Long eventWinnerId,
        @NotNull @Positive
        Double betAmount
) {
    public Bet toBet() {
        return new Bet(betId, userId, eventId, eventMarketId, eventWinnerId, betAmount);
    }
}
