package com.sporty.assignment.repository;

import com.sporty.assignment.model.Bet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class InMemoryBetRepository implements BetRepository {
    private final CopyOnWriteArrayList<Bet> bets = new CopyOnWriteArrayList<>();

    @Override
    public void save(Bet bet) {
        bets.add(bet);
    }

    @Override
    public List<Bet> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(bets));
    }

    @Override
    public List<Bet> findByEventId(long eventId) {
        return bets.stream()
                .filter(bet -> bet.eventId() == eventId)
                .collect(Collectors.toUnmodifiableList());
    }
}

