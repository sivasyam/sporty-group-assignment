package com.sporty.assignment.config;

import com.sporty.assignment.broker.MockKafkaBroker;
import com.sporty.assignment.broker.MockRocketMqBroker;
import com.sporty.assignment.model.Bet;
import com.sporty.assignment.repository.BetRepository;
import com.sporty.assignment.repository.InMemoryBetRepository;
import com.sporty.assignment.service.BetSettlementService;
import com.sporty.assignment.service.EventOutcomeConsumer;
import com.sporty.assignment.service.EventOutcomePublisher;
import com.sporty.assignment.service.MockRocketMqSettlementProducer;
import com.sporty.assignment.service.SettlementConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public BetRepository betRepository() {
        InMemoryBetRepository repository = new InMemoryBetRepository();
        List.of(
                new Bet(1L, 10L, 100L, 5L, 2L, 25.0),
                new Bet(2L, 11L, 100L, 6L, 3L, 40.0),
                new Bet(3L, 12L, 200L, 7L, 4L, 60.0)
        ).forEach(repository::save);
        return repository;
    }

    @Bean
    public MockKafkaBroker kafkaBroker() {
        return new MockKafkaBroker();
    }

    @Bean
    public MockRocketMqBroker rocketMqBroker() {
        return new MockRocketMqBroker();
    }

    @Bean
    public MockRocketMqSettlementProducer settlementProducer(MockRocketMqBroker rocketMqBroker) {
        return new MockRocketMqSettlementProducer(rocketMqBroker);
    }

    @Bean
    public BetSettlementService betSettlementService(BetRepository betRepository,
                                                     MockRocketMqSettlementProducer settlementProducer) {
        return new BetSettlementService(betRepository, settlementProducer);
    }

    @Bean
    public EventOutcomePublisher eventOutcomePublisher(MockKafkaBroker kafkaBroker) {
        return new EventOutcomePublisher(kafkaBroker);
    }

    @Bean
    public EventOutcomeConsumer eventOutcomeConsumer(MockKafkaBroker kafkaBroker,
                                                     BetSettlementService betSettlementService) {
        EventOutcomeConsumer consumer = new EventOutcomeConsumer(kafkaBroker, betSettlementService);
        consumer.start();
        return consumer;
    }

    @Bean
    public SettlementConsumer settlementConsumer(MockRocketMqBroker rocketMqBroker) {
        SettlementConsumer consumer = new SettlementConsumer(rocketMqBroker);
        consumer.start();
        return consumer;
    }
}

