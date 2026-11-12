
package com.cap51.cap51_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cutover")
public class CutoverProperties {

    private String domain;
    private String oldEndpoint;
    private String newEndpoint;
    private String activeEnvironment;
    private int ttlSeconds;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getOldEndpoint() {
        return oldEndpoint;
    }

    public void setOldEndpoint(String oldEndpoint) {
        this.oldEndpoint = oldEndpoint;
    }

    public String getNewEndpoint() {
        return newEndpoint;
    }

    public void setNewEndpoint(String newEndpoint) {
        this.newEndpoint = newEndpoint;
    }

    public String getActiveEnvironment() {
        return activeEnvironment;
    }

    public void setActiveEnvironment(String activeEnvironment) {
        this.activeEnvironment = activeEnvironment;
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}
