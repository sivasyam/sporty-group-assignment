package com.sporty.assignment.model;

public record EventOutcome(
        Long eventId,
        String eventName,
        Long eventWinnerId
) {
}
