
package com.cap51.cap51_backend.controller;

import com.cap51.cap51_backend.service.EnvironmentHealthService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/environments")
public class EnvironmentHealthController {

    private final EnvironmentHealthService healthService;

    public EnvironmentHealthController(
            EnvironmentHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/{environment}/health")
    public Map<String, Object> checkHealth(
            @PathVariable String environment) {

        return healthService.checkHealth(environment);
    }

    @PostMapping("/{environment}/health")
    public Map<String, Object> updateHealth(
            @PathVariable String environment,
            @RequestParam String status) {

        try {
            return healthService.updateHealth(
                    environment, status);
        } catch (IllegalArgumentException exception) {
            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", exception.getMessage()
            );
        }
    }
}
