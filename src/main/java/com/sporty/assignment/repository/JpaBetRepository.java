package com.sporty.assignment.repository;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.entity.BetEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaBetRepository implements BetRepository {
    private final BetJpaRepository betJpaRepository;

    public JpaBetRepository(BetJpaRepository betJpaRepository) {
        this.betJpaRepository = betJpaRepository;
    }

    @Override
    public void save(Bet bet) {
        betJpaRepository.save(toEntity(bet));
    }

    @Override
    public List<Bet> findAll() {
        return betJpaRepository.findAll().stream().map(JpaBetRepository::toDomain).toList();
    }

    @Override
    public List<Bet> findByEventId(long eventId) {
        return betJpaRepository.findByEventId(eventId).stream().map(JpaBetRepository::toDomain).toList();
    }

    private static BetEntity toEntity(Bet bet) {
        return new BetEntity(
                bet.betId(),
                bet.userId(),
                bet.eventId(),
                bet.eventMarketId(),
                bet.eventWinnerId(),
                bet.betAmount()
        );
    }

    private static Bet toDomain(BetEntity entity) {
        return new Bet(
                entity.getBetId(),
                entity.getUserId(),
                entity.getEventId(),
                entity.getEventMarketId(),
                entity.getEventWinnerId(),
                entity.getBetAmount()
        );
    }
}
