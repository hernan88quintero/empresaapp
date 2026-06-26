package com.hernan.empresaapp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class ExchangeClientConfig {

    @Bean
    public WebClient exchangeWebClient(ExchangeProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        Math.toIntExact(properties.provider().connectTimeout().toMillis()))
                .doOnConnected(connection -> connection.addHandlerLast(
                        new ReadTimeoutHandler(properties.provider().responseTimeout().toMillis(), TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(properties.provider().baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public CacheManager cacheManager(ExchangeProperties properties) {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "exchangeLatest", "exchangePair", "exchangeProviderLatest");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(properties.cache().ttl())
                .maximumSize(properties.cache().maximumSize()));
        return manager;
    }
}
