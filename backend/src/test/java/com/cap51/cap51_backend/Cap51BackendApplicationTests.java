
package com.cap51.cap51_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.cap51.cap51_backend.service.DnsResolutionService;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.UnknownHostException;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Cap51BackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;
	@MockitoBean
 	private DnsResolutionService dnsResolutionService;
    @Test
    void healthEndpointShouldWork() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void environmentEndpointShouldWork() throws Exception {
        mockMvc.perform(get("/api/environment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("CAP-51"));
    }

    @Test
    void dnsDiagnosticsEndpointShouldWork() throws Exception {
        mockMvc.perform(get("/api/dns/diagnostics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosticStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.applicationStatus").value("UP"));
    }
	
@Test
void dnsComparisonShouldReturnMatch() throws Exception {

    when(dnsResolutionService.resolve("match.example.com"))
            .thenReturn(List.of("192.0.2.10"));

    mockMvc.perform(get("/api/dns/compare")
                    .param("domain", "match.example.com")
                    .param("expectedIp", "192.0.2.10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.comparisonStatus").value("MATCH"));
}

@Test
void dnsComparisonShouldReturnMismatch() throws Exception {

    when(dnsResolutionService.resolve("mismatch.example.com"))
            .thenReturn(List.of("192.0.2.20"));

    mockMvc.perform(get("/api/dns/compare")
                    .param("domain", "mismatch.example.com")
                    .param("expectedIp", "192.0.2.10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.comparisonStatus").value("MISMATCH"));
}

@Test
void dnsComparisonShouldReturnResolutionFailed() throws Exception {

    when(dnsResolutionService.resolve("failed.example.com"))
            .thenThrow(new UnknownHostException("Domain not found"));

    mockMvc.perform(get("/api/dns/compare")
                    .param("domain", "failed.example.com")
                    .param("expectedIp", "192.0.2.10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.comparisonStatus")
                    .value("DNS_RESOLUTION_FAILED"));
}
@Test
void dnsComparisonShouldRejectInvalidDomain() throws Exception {

    mockMvc.perform(get("/api/dns/compare")
                    .param("domain", "invalid_domain")
                    .param("expectedIp", "192.0.2.10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.comparisonStatus")
                    .value("INVALID_DOMAIN"));
}
@Test
void dnsComparisonShouldRejectInvalidIp() throws Exception {

    mockMvc.perform(get("/api/dns/compare")
                    .param("domain", "google.com")
                    .param("expectedIp", "999.999.999.999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.comparisonStatus")
                    .value("INVALID_IP"));
}

}
