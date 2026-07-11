package com.sporty.assignment.controller;

import com.sporty.assignment.api.request.BetRequest;
import com.sporty.assignment.api.request.EventOutcomeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EmbeddedKafka(partitions = 1, topics = "event-outcomes")
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
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
                        .content(objectMapper.writeValueAsString(BetRequest.builder()
                                .betId(501L)
                                .userId(33L)
                                .eventId(eventId)
                                .eventMarketId(8L)
                                .eventWinnerId(4L)
                                .betAmount(99.0)
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").value(501))
                .andExpect(jsonPath("$.eventId").value(eventId));

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(EventOutcomeRequest.builder()
                                .eventId(eventId)
                                .eventName("Integration Match")
                                .eventWinnerId(4L)
                                .build())))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventWinnerId").value(4));

        waitForSettlementCount(1);
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

    @Test
    void duplicateEventOutcomeDoesNotCreateDuplicateSettlements() throws Exception {
        long eventId = 902L;

        mockMvc.perform(post("/api/bets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(BetRequest.builder()
                                .betId(601L)
                                .userId(44L)
                                .eventId(eventId)
                                .eventMarketId(8L)
                                .eventWinnerId(5L)
                                .betAmount(75.0)
                                .build())))
                .andExpect(status().isCreated());

        String payload = objectMapper.writeValueAsString(EventOutcomeRequest.builder()
                .eventId(eventId)
                .eventName("Duplicate Match")
                .eventWinnerId(5L)
                .build());

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isAccepted());

        mockMvc.perform(post("/api/event-outcomes")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isAccepted());

        waitForSettlementCount(1);
        assertSettlementCountStable(1, 1000);
        assertThat(settlementConsumer.received()).hasSize(1);
        mockMvc.perform(get("/api/settlements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].betId").value(601))
                .andExpect(jsonPath("$[0].eventId").value((int) eventId));
    }

    @Test
    void invalidBetPayloadIsRejected() throws Exception {
        mockMvc.perform(post("/api/bets")
                        .contentType("application/json")
                        .content("{\"betId\":0,\"userId\":33,\"eventId\":901,\"eventMarketId\":8,\"eventWinnerId\":4,\"betAmount\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]").exists());
    }

    @Test
    void invalidEventOutcomePayloadIsRejected() throws Exception {
        mockMvc.perform(post("/api/event-outcomes")
                        .contentType("application/json")
                        .content("{\"eventId\":0,\"eventName\":\"\",\"eventWinnerId\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]").exists());
    }

    private void waitForSettlementCount(int expectedSize) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < deadline) {
            if (settlementConsumer.received().size() >= expectedSize) {
                return;
            }
            Thread.sleep(100);
        }
        throw new AssertionError("Timed out waiting for settlements");
    }

    private void assertSettlementCountStable(int expectedSize, long stableMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + stableMillis;
        while (System.currentTimeMillis() < deadline) {
            int currentSize = settlementConsumer.received().size();
            if (currentSize > expectedSize) {
                throw new AssertionError("Expected " + expectedSize + " settlements, found " + currentSize);
            }
            Thread.sleep(50);
        }
    }
}
