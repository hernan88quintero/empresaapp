package com.hernan.empresaapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "exchange")
public record ExchangeProperties(
        Provider provider,
        Cache cache,
        Scheduler scheduler,
        List<String> supportedCurrencies
) {
    public record Provider(String baseUrl, Duration connectTimeout, Duration responseTimeout) {}
    public record Cache(Duration ttl, long maximumSize) {}
    public record Scheduler(boolean enabled, String cron, String zone) {}
}
