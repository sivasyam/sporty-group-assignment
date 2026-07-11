package com.sporty.assignment.service;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.model.SettlementStatus;
import com.sporty.assignment.repository.BetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class BetSettlementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BetSettlementService.class);
    private final BetRepository betRepository;
    private final SettlementProducer settlementProducer;

    public BetSettlementService(BetRepository betRepository, SettlementProducer settlementProducer) {
        this.betRepository = betRepository;
        this.settlementProducer = settlementProducer;
    }

    public List<SettlementMessage> settle(EventOutcome outcome) {
        List<SettlementMessage> settlements = new ArrayList<>();
        List<Bet> bets = betRepository.findByEventId(outcome.eventId());
        LOGGER.info("event=settlement_started event_id={} matched_bets={}", outcome.eventId(), bets.size());
        for (Bet bet : bets) {
            SettlementStatus status = bet.eventWinnerId() == outcome.eventWinnerId()
                    ? SettlementStatus.WON
                    : SettlementStatus.LOST;
            SettlementMessage settlement = SettlementMessage.from(bet, outcome, status);
            LOGGER.info(
                    "event=settling_bet bet_id={} user_id={} event_id={} market_id={} bet_winner_id={} outcome_winner_id={} status={}",
                    bet.betId(),
                    bet.userId(),
                    bet.eventId(),
                    bet.eventMarketId(),
                    bet.eventWinnerId(),
                    outcome.eventWinnerId(),
                    status
            );
            settlementProducer.send(settlement);
            settlements.add(settlement);
        }
        LOGGER.info("event=settlement_completed event_id={} settlements={}", outcome.eventId(), settlements.size());
        return settlements;
    }
}
