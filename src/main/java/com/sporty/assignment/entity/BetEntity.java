package com.sporty.assignment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "bet_amount", nullable = false)
    private Double betAmount;

    protected BetEntity() {
    }

    public BetEntity(Long betId, Long userId, Long eventId, Long eventMarketId, Long eventWinnerId, Double betAmount) {
        this.betId = betId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventMarketId = eventMarketId;
        this.eventWinnerId = eventWinnerId;
        this.betAmount = betAmount;
    }

    public Long getBetId() {
        return betId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getEventMarketId() {
        return eventMarketId;
    }

    public Long getEventWinnerId() {
        return eventWinnerId;
    }

    public Double getBetAmount() {
        return betAmount;
    }
}
