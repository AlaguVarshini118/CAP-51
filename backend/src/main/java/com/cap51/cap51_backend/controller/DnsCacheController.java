
package com.cap51.cap51_backend.controller;

import com.cap51.cap51_backend.service.DnsCacheService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Map;

@RestController
@RequestMapping("/api/dns/cache")
public class DnsCacheController {

    private final DnsCacheService dnsCacheService;

    public DnsCacheController(DnsCacheService dnsCacheService) {
        this.dnsCacheService = dnsCacheService;
    }

    @GetMapping("/resolve")
    public Map<String, Object> resolveDns(
            @RequestParam String resolverId) {

        if (resolverId.isBlank() || resolverId.length() > 50) {
            return Map.of(
                    "status", "INVALID_RESOLVER",
                    "message", "Resolver ID must contain 1 to 50 characters"
            );
        }

        return dnsCacheService.resolve(resolverId);
    }
    
@DeleteMapping("/clear")
public Map<String, Object> clearCache(
        @RequestParam String resolverId) {

    if (resolverId.isBlank() || resolverId.length() > 50) {
        return Map.of(
                "status", "INVALID_RESOLVER",
                "message", "Resolver ID must contain 1 to 50 characters"
        );
    }

    return dnsCacheService.clearCache(resolverId);
}


    @PostMapping("/record")
    public Map<String, Object> updateDnsRecord(
            @RequestParam String environment) {

        try {
            return dnsCacheService.updateDnsRecord(environment);

        } catch (IllegalArgumentException exception) {
            return Map.of(
                    "status", "INVALID_ENVIRONMENT",
                    "message", exception.getMessage()
            );
        }
    }

    @GetMapping("/status")
    public Map<String, Object> getDnsStatus() {

        return dnsCacheService.getDnsStatus();
    }
}
