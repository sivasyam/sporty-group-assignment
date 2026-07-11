package com.sporty.assignment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "bets")
public class BetEntity {
    @Id
    @Column(name = "bet_id", nullable = false)
    private Long betId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "event_market_id", nullable = false)
    private Long eventMarketId;

    @Column(name = "event_winner_id", nullable = false)
    private Long eventWinnerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal betAmount;

    protected BetEntity() {
    }

    public BetEntity(Long betId, Long userId, Long eventId, Long eventMarketId, Long eventWinnerId, BigDecimal betAmount) {
        this.betId = betId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventMarketId = eventMarketId;
        this.eventWinnerId = eventWinnerId;
        this.betAmount = betAmount;
    }

}
