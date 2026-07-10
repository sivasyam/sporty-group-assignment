package com.sporty.assignment.controller;

import com.sporty.assignment.model.SettlementMessage;
import com.sporty.assignment.service.SettlementConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
public class SettlementsRestController {
    private final SettlementConsumer settlementConsumer;

    public SettlementsRestController(SettlementConsumer settlementConsumer) {
        this.settlementConsumer = settlementConsumer;
    }

    @GetMapping
    public List<SettlementMessage> listSettlements() {
        return settlementConsumer.received();
    }
}

