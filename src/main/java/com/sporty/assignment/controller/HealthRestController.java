package com.sporty.assignment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthRestController {
    @GetMapping
    public StatusResponse health() {
        return new StatusResponse("up");
    }

    public record StatusResponse(String status) {
    }
}

