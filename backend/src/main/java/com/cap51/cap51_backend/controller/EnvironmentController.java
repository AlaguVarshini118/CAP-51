
package com.cap51.cap51_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EnvironmentController {

    @Value("${app.environment}")
    private String environment;

    @GetMapping("/api/environment")
    public Map<String, String> getEnvironment() {

        return Map.of(
                "application", "CAP-51",
                "environment", environment,
                "message", "Environment identification successful"
        );
    }
}
