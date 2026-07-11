package com.sporty.assignment.api.request;

import com.sporty.assignment.model.EventOutcome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record EventOutcomeRequest(
        @NotNull @Positive
        Long eventId,
        @NotBlank
        String eventName,
        @NotNull @Positive
        Long eventWinnerId
) {
    public EventOutcome toEventOutcome() {
        return new EventOutcome(eventId, eventName, eventWinnerId);
    }
}
