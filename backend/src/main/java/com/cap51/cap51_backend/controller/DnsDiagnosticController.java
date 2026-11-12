
package com.cap51.cap51_backend.controller;

import com.cap51.cap51_backend.service.DnsResolutionService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.net.Inet6Address;
import java.net.InetAddress;

@RestController
public class DnsDiagnosticController {

    private final DnsResolutionService dnsResolutionService;

    @Value("${app.environment}")
    private String environment;

    public DnsDiagnosticController(
            DnsResolutionService dnsResolutionService) {
        this.dnsResolutionService = dnsResolutionService;
    }

    @GetMapping("/api/dns/diagnostics")
    public Map<String, Object> getDiagnostics() {

        return Map.of(
                "application", "CAP-51",
                "applicationStatus", "UP",
                "environment", environment,
                "diagnosticStatus", "SUCCESS",
                "message", "Backend diagnostic completed",
                "timestamp", Instant.now().toString()
        );
    }

    @GetMapping("/api/dns/resolve")
    public Map<String, Object> resolveDns(
            @RequestParam String domain) {
                if (!isValidDomain(domain)) {
    return Map.of(
            "domain", domain,
            "resolutionStatus", "INVALID_DOMAIN",
            "resolvedAddresses", List.of(),
            "message", "Please provide a valid domain name"
    );
}

        try {
            List<String> addresses =
                    dnsResolutionService.resolve(domain);

            return Map.of(
                    "domain", domain,
                    "resolutionStatus", "SUCCESS",
                    "resolvedAddresses", addresses,
                    "message", "DNS resolution successful",
                    "timestamp", Instant.now().toString()
            );

        } catch (UnknownHostException e) {

            return Map.of(
                    "domain", domain,
                    "resolutionStatus", "FAILED",
                    "resolvedAddresses", List.of(),
                    "message", "Unable to resolve domain",
                    "timestamp", Instant.now().toString()
            );
        }
    }

    @GetMapping("/api/dns/compare")
    public Map<String, Object> compareDns(
            @RequestParam String domain,
            @RequestParam String expectedIp) {
                if (!isValidDomain(domain)) {
    return Map.of(
            "domain", domain,
            "comparisonStatus", "INVALID_DOMAIN",
            "message", "Please provide a valid domain name"
    );
}

if (!isValidIpAddress(expectedIp)) {
    return Map.of(
            "domain", domain,
            "expectedIp", expectedIp,
            "comparisonStatus", "INVALID_IP",
            "message", "Please provide a valid IP address"
    );
}

        try {
            List<String> addresses =
                    dnsResolutionService.resolve(domain);

            boolean matches = addresses.contains(expectedIp.trim());

            return Map.of(
                    "domain", domain,
                    "expectedIp", expectedIp,
                    "actualIpAddresses", addresses,
                    "comparisonStatus", matches
                            ? "MATCH" : "MISMATCH",
                    "message", matches
                            ? "DNS points to the expected IP address"
                            : "DNS does not point to the expected IP address",
                    "timestamp", Instant.now().toString()
            );

        } catch (UnknownHostException e) {

            return Map.of(
                    "domain", domain,
                    "expectedIp", expectedIp,
                    "actualIpAddresses", List.of(),
                    "comparisonStatus", "DNS_RESOLUTION_FAILED",
                    "message", "Unable to resolve the domain",
                    "timestamp", Instant.now().toString()
            );
        }
    }
    private boolean isValidDomain(String domain) {

    if (domain == null || domain.isBlank()
            || domain.length() > 253) {
        return false;
    }

    return domain.matches(
            "(?i)^(?=.{1,253}$)([a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z]{2,63}$"
    );
}

private boolean isValidIpAddress(String ip) {

    if (ip == null || ip.isBlank()) {
        return false;
    }

    // Validate IPv4
    if (ip.matches("[0-9.]+")) {

        String[] parts = ip.split("\\.", -1);

        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {

                if (part.isEmpty() || part.length() > 3) {
                    return false;
                }

                int number = Integer.parseInt(part);

                if (number < 0 || number > 255) {
                    return false;
                }
            }

            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validate IPv6
    if (ip.contains(":")) {
        try {
            return InetAddress.getByName(ip) instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    return false;
}
}
