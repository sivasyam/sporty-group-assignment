package com.sporty.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.assignment.model.Bet;
import com.sporty.assignment.model.EventOutcome;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SportyAssignmentIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.sporty.assignment.service.SettlementConsumer settlementConsumer;

    @Test
    void healthEndpointIsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("up"));
    }

    @Test
    void publishingOutcomeSettlesMatchingBetsThroughTheFullFlow() throws Exception {
        long eventId = 901L;

        mockMvc.perform(post("/api/bets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new Bet(501L, 33L, eventId, 8L, 4L, 99.0))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").value(501))
                .andExpect(jsonPath("$.eventId").value(eventId));

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new EventOutcome(eventId, "Integration Match", 4L))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventWinnerId").value(4));

        assertThat(settlementConsumer.received())
                .hasSizeGreaterThan(0)
                .anySatisfy(message -> {
                    assertThat(message.betId()).isEqualTo(501);
                    assertThat(message.eventId()).isEqualTo(eventId);
                    assertThat(message.status()).isEqualTo(com.sporty.assignment.model.SettlementStatus.WON);
                });

        mockMvc.perform(get("/api/settlements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].betId").value(501))
                .andExpect(jsonPath("$[0].eventId").value((int) eventId))
                .andExpect(jsonPath("$[0].status").value("WON"));
    }
}
