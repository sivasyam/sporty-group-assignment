package com.sporty.assignment.config;

import com.sporty.assignment.broker.MockRocketMqBroker;
import com.sporty.assignment.repository.BetRepository;
import com.sporty.assignment.service.BetSettlementService;
import com.sporty.assignment.service.MockRocketMqSettlementProducer;
import com.sporty.assignment.service.SettlementConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
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
    public SettlementConsumer settlementConsumer(MockRocketMqBroker rocketMqBroker) {
        SettlementConsumer consumer = new SettlementConsumer(rocketMqBroker);
        consumer.start();
        return consumer;
    }
}
