package com.cap51.cap51_backend.service;

import com.cap51.cap51_backend.config.CutoverProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
@Service
public class CutoverService {

    private final CutoverProperties properties;
    private final EnvironmentHealthService healthService;
    private String activeEnvironment;
    private final List<Map<String, Object>> cutoverHistory =
        new ArrayList<>();
        private void recordHistory(
        String operation,
        String previousEnvironment,
        String newEnvironment) {

    cutoverHistory.add(Map.of(
            "operation", operation,
            "previousEnvironment", previousEnvironment,
            "newEnvironment", newEnvironment,
            "timestamp", Instant.now().toString()
    ));
}

    public CutoverService(
        CutoverProperties properties,
        EnvironmentHealthService healthService) {

    this.properties = properties;
    this.healthService = healthService;

    this.activeEnvironment =
            properties.getActiveEnvironment().toUpperCase();
}

    public synchronized Map<String, Object> getStatus() {
        return Map.of(
                "domain", properties.getDomain(),
                "activeEnvironment", activeEnvironment,
                "activeEndpoint", getActiveEndpoint(),
                "ttlSeconds", properties.getTtlSeconds(),
                "timestamp", Instant.now().toString()
        );
    }

    public synchronized Map<String, Object> cutoverToNew() {

    Map<String, Object> health =
            healthService.checkHealth("NEW");

    if (!Boolean.TRUE.equals(health.get("healthy"))) {

        return Map.of(
                "operation", "CUTOVER",
                "status", "BLOCKED",
                "activeEnvironment", activeEnvironment,
                "activeEndpoint", getActiveEndpoint(),
                "message", "Cutover blocked because NEW environment is unhealthy",
                "healthCheck", health,
                "timestamp", Instant.now().toString()
        );
    }

    String previousEnvironment = activeEnvironment;

    activeEnvironment = "NEW";

    recordHistory(
            "CUTOVER",
            previousEnvironment,
            activeEnvironment
    );

    return Map.of(
            "operation", "CUTOVER",
            "status", "SUCCESS",
            "activeEnvironment", activeEnvironment,
            "activeEndpoint", getActiveEndpoint(),
            "message", "Cutover completed successfully",
            "timestamp", Instant.now().toString()
    );
}

public synchronized Map<String, Object> rollback() {

    Map<String, Object> health =
            healthService.checkHealth("OLD");

    if (!Boolean.TRUE.equals(health.get("healthy"))) {

        return Map.of(
                "operation", "ROLLBACK",
                "status", "BLOCKED",
                "activeEnvironment", activeEnvironment,
                "activeEndpoint", getActiveEndpoint(),
                "message", "Rollback blocked because OLD environment is unhealthy",
                "healthCheck", health,
                "timestamp", Instant.now().toString()
        );
    }

    String previousEnvironment = activeEnvironment;

    activeEnvironment = "OLD";

    recordHistory(
            "ROLLBACK",
            previousEnvironment,
            activeEnvironment
    );

    return Map.of(
            "operation", "ROLLBACK",
            "status", "SUCCESS",
            "activeEnvironment", activeEnvironment,
            "activeEndpoint", getActiveEndpoint(),
            "message", "Rollback completed successfully",
            "timestamp", Instant.now().toString()
    );
}
public synchronized List<Map<String, Object>> getHistory() {

    return new ArrayList<>(cutoverHistory);
}

    private String getActiveEndpoint() {
        if ("NEW".equals(activeEnvironment)) {
            return properties.getNewEndpoint();
        }

        return properties.getOldEndpoint();
    }
}