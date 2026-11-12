
package com.cap51.cap51_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @Value("${app.environment:local}")
    private String environment;

    @GetMapping("/api/health")
    public Map<String, Object> checkHealth() {

        return Map.of(
                "application", "CAP-51",
                "status", "UP",
                "environment", environment,
                "message", "Application is healthy",
                "timestamp", Instant.now().toString()
        );
    }
}
