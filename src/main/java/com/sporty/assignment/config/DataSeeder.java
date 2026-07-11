package com.sporty.assignment.config;

import com.sporty.assignment.model.Bet;
import com.sporty.assignment.repository.BetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    public CommandLineRunner seedBets(BetRepository betRepository) {
        return args -> {
            if (!betRepository.findAll().isEmpty()) {
                return;
            }

            betRepository.save(new Bet(1L, 10L, 100L, 5L, 2L, 25.0));
            betRepository.save(new Bet(2L, 11L, 100L, 6L, 3L, 40.0));
            betRepository.save(new Bet(3L, 12L, 200L, 7L, 4L, 60.0));
        };
    }
}
