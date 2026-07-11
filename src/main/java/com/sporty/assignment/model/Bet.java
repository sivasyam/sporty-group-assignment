package com.sporty.assignment.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Bet(
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
        BigDecimal betAmount
) {
}
