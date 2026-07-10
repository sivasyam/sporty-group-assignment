package com.sporty.assignment.service;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.model.SettlementStatus;
import com.sporty.assignment.repository.BetRepository;

import java.util.ArrayList;
import java.util.List;

public final class BetSettlementService {
    private final BetRepository betRepository;
    private final SettlementProducer settlementProducer;

    public BetSettlementService(BetRepository betRepository, SettlementProducer settlementProducer) {
        this.betRepository = betRepository;
        this.settlementProducer = settlementProducer;
    }

    public List<SettlementMessage> settle(EventOutcome outcome) {
        List<SettlementMessage> settlements = new ArrayList<>();
        for (Bet bet : betRepository.findByEventId(outcome.eventId())) {
            SettlementStatus status = bet.eventWinnerId() == outcome.eventWinnerId()
                    ? SettlementStatus.WON
                    : SettlementStatus.LOST;
            SettlementMessage settlement = SettlementMessage.from(bet, outcome, status);
            settlementProducer.send(settlement);
            settlements.add(settlement);
        }
        return settlements;
    }
}

