package com.sporty.assignment.controller;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.repository.BetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
public class BetsRestController {
    private final BetRepository betRepository;

    public BetsRestController(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Bet createBet(@RequestBody Bet bet) {
        betRepository.save(bet);
        return bet;
    }

    @GetMapping
    public List<Bet> listBets() {
        return betRepository.findAll();
    }
}

