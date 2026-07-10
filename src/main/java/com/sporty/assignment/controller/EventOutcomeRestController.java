package com.sporty.assignment.controller;

import com.sporty.assignment.model.EventOutcome;
import com.sporty.assignment.service.EventOutcomePublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-outcomes")
public class EventOutcomeRestController {
    private final EventOutcomePublisher publisher;

    public EventOutcomeRestController(EventOutcomePublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EventOutcome publishOutcome(@RequestBody EventOutcome outcome) {
        publisher.publish(outcome);
        return outcome;
    }
}
