package com.sporty.assignment.repository;

import com.sporty.assignment.model.Bet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaBetRepository.class)
class JpaBetRepositoryTest {
    @Autowired
    private JpaBetRepository betRepository;

    @Test
    void savesAndLoadsBetsFromH2() {
        Bet bet = new Bet(10L, 20L, 300L, 4L, 9L, 55.0);

        betRepository.save(bet);

        assertThat(betRepository.findAll()).containsExactly(bet);
        assertThat(betRepository.findByEventId(300L)).containsExactly(bet);
        assertThat(betRepository.findByEventId(999L)).isEmpty();
    }
}
