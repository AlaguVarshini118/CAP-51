
package com.cap51.cap51_backend.service;

import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Service
public class DnsResolutionService {

    public List<String> resolve(String domain)
            throws UnknownHostException {

        InetAddress[] addresses =
                InetAddress.getAllByName(domain);

        return Arrays.stream(addresses)
                .map(InetAddress::getHostAddress)
                .toList();
    }
}
