package com.sporty.assignment.repository;

import com.sporty.assignment.model.Bet;

import java.util.List;

public interface BetRepository {
    void save(Bet bet);

    List<Bet> findAll();

    List<Bet> findByEventId(long eventId);
}

