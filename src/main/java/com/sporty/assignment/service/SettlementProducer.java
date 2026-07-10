package com.sporty.assignment.service;

import com.sporty.assignment.model.SettlementMessage;

public interface SettlementProducer {
    void send(SettlementMessage settlementMessage);
}

