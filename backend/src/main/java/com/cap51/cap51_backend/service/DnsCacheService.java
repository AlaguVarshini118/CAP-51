
package com.cap51.cap51_backend.service;

import com.cap51.cap51_backend.config.CutoverProperties;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DnsCacheService {

    private final CutoverProperties properties;

    private String authoritativeEnvironment = "OLD";

    private final Map<String, CacheEntry> resolverCache =
            new ConcurrentHashMap<>();

    public DnsCacheService(CutoverProperties properties) {
        this.properties = properties;
    }

    public synchronized Map<String, Object> resolve(
            String resolverId) {

        Instant now = Instant.now();

        CacheEntry cached = resolverCache.get(resolverId);

        if (cached != null && now.isBefore(cached.expiresAt)) {

            return Map.of(
                    "resolver", resolverId,
                    "domain", properties.getDomain(),
                    "resolvedEndpoint", cached.endpoint,
                    "source", "CACHE",
                    "cacheStatus", "HIT",
                    "expiresAt", cached.expiresAt.toString(),
                    "timestamp", now.toString()
            );
        }

        String endpoint = getAuthoritativeEndpoint();

        Instant expiresAt = now.plusSeconds(
                properties.getTtlSeconds()
        );

        resolverCache.put(
                resolverId,
                new CacheEntry(endpoint, expiresAt)
        );

        return Map.of(
                "resolver", resolverId,
                "domain", properties.getDomain(),
                "resolvedEndpoint", endpoint,
                "source", "AUTHORITATIVE_DNS_SIMULATION",
                "cacheStatus", "MISS",
                "expiresAt", expiresAt.toString(),
                "timestamp", now.toString()
        );
    }

    public synchronized Map<String, Object> updateDnsRecord(
            String environment) {

        String newEnvironment = environment.toUpperCase();

        if (!newEnvironment.equals("OLD")
                && !newEnvironment.equals("NEW")) {

            throw new IllegalArgumentException(
                    "Environment must be OLD or NEW"
            );
        }

        String previousEndpoint =
                getAuthoritativeEndpoint();

        authoritativeEnvironment = newEnvironment;

        return Map.of(
                "domain", properties.getDomain(),
                "previousEndpoint", previousEndpoint,
                "newEndpoint", getAuthoritativeEndpoint(),
                "authoritativeEnvironment", authoritativeEnvironment,
                "message", "Simulated authoritative DNS record updated",
                "timestamp", Instant.now().toString()
        );
    }

    public synchronized Map<String, Object> getDnsStatus() {

        return Map.of(
                "domain", properties.getDomain(),
                "authoritativeEnvironment", authoritativeEnvironment,
                "authoritativeEndpoint", getAuthoritativeEndpoint(),
                "ttlSeconds", properties.getTtlSeconds(),
                "cachedResolvers", resolverCache.size(),
                "timestamp", Instant.now().toString()
        );
    }

    private String getAuthoritativeEndpoint() {

        if ("NEW".equals(authoritativeEnvironment)) {
            return properties.getNewEndpoint();
        }

        return properties.getOldEndpoint();
    }

    private static class CacheEntry {

        private final String endpoint;
        private final Instant expiresAt;

        private CacheEntry(String endpoint, Instant expiresAt) {
            this.endpoint = endpoint;
            this.expiresAt = expiresAt;
        }
    }
    
public synchronized Map<String, Object> clearCache(String resolverId) {

    CacheEntry removed = resolverCache.remove(resolverId);

    if (removed == null) {
        return Map.of(
                "resolver", resolverId,
                "status", "NOT_FOUND",
                "message", "No cached DNS entry found for this resolver",
                "timestamp", Instant.now().toString()
        );
    }

    return Map.of(
            "resolver", resolverId,
            "status", "CLEARED",
            "message", "Resolver cache cleared successfully",
            "timestamp", Instant.now().toString()
    );
}

}
