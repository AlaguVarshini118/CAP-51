
package com.cap51.cap51_backend.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EnvironmentHealthService {

    private final Map<String, String> environmentStatus =
            new ConcurrentHashMap<>();

    public EnvironmentHealthService() {
        environmentStatus.put("OLD", "UP");
        environmentStatus.put("NEW", "UP");
    }

    public synchronized Map<String, Object> checkHealth(
            String environment) {

        String name = environment.toUpperCase();
        String status = environmentStatus.get(name);

        if (status == null) {
            return Map.of(
                    "environment", name,
                    "status", "UNKNOWN",
                    "message", "Environment does not exist",
                    "timestamp", Instant.now().toString()
            );
        }

        return Map.of(
                "environment", name,
                "status", status,
                "healthy", "UP".equals(status),
                "timestamp", Instant.now().toString()
        );
    }

    public synchronized Map<String, Object> updateHealth(
            String environment, String status) {

        String name = environment.toUpperCase();
        String health = status.toUpperCase();

        if (!environmentStatus.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Invalid environment: " + name);
        }

        if (!health.equals("UP") && !health.equals("DOWN")) {
            throw new IllegalArgumentException(
                    "Status must be UP or DOWN");
        }

        environmentStatus.put(name, health);

        return checkHealth(name);
    }
}
