package com.hernan.empresaapp.venezuelaexchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "venezuela-exchange")
public record VenezuelaExchangeProperties(
        Provider provider,
        Scheduler scheduler,
        Cache cache) {

    public record Provider(String baseUrl, Duration connectTimeout, Duration responseTimeout) {}

    public record Scheduler(boolean enabled, String cron, String zone) {}

    public record Cache(Duration ttl, long maximumSize) {}
}
