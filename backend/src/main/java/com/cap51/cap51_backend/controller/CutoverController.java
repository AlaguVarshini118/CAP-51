
package com.cap51.cap51_backend.controller;

import com.cap51.cap51_backend.config.CutoverProperties;
import com.cap51.cap51_backend.service.CutoverService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CutoverController {

    private final CutoverProperties cutoverProperties;
    private final CutoverService cutoverService;

    public CutoverController(
            CutoverProperties cutoverProperties,
            CutoverService cutoverService) {

        this.cutoverProperties = cutoverProperties;
        this.cutoverService = cutoverService;
    }

    @GetMapping("/api/cutover/config")
    public Map<String, Object> getCutoverConfig() {
        return Map.of(
                "domain", cutoverProperties.getDomain(),
                "oldEndpoint", cutoverProperties.getOldEndpoint(),
                "newEndpoint", cutoverProperties.getNewEndpoint(),
                "activeEnvironment", cutoverProperties.getActiveEnvironment(),
                "ttlSeconds", cutoverProperties.getTtlSeconds()
        );
    }

    @GetMapping("/api/cutover/status")
    public Map<String, Object> getCutoverStatus() {
        return cutoverService.getStatus();
    }

    @PostMapping("/api/cutover/switch")
    public Map<String, Object> performCutover() {
        return cutoverService.cutoverToNew();
    }

    @PostMapping("/api/cutover/rollback")
    public Map<String, Object> rollbackCutover() {
        return cutoverService.rollback();
    }

    @GetMapping("/api/cutover/history")
    public Map<String, Object> getCutoverHistory() {
        return Map.of(
                "history", cutoverService.getHistory(),
                "totalOperations", cutoverService.getHistory().size()
        );
    }
}
