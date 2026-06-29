package com.hernan.empresaapp.venezuelaexchange.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class VenezuelaExchangeConfig {

    @Bean
    @Qualifier("venezuelaExchangeWebClient")
    WebClient venezuelaExchangeWebClient(VenezuelaExchangeProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        Math.toIntExact(properties.provider().connectTimeout().toMillis()))
                .doOnConnected(connection -> connection.addHandlerLast(
                        new ReadTimeoutHandler(
                                properties.provider().responseTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                .baseUrl(properties.provider().baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean("venezuelaExchangeCacheManager")
    CacheManager venezuelaExchangeCacheManager(VenezuelaExchangeProperties properties) {
        CaffeineCacheManager manager = new CaffeineCacheManager("venezuelaLatestRemote");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(properties.cache().ttl().toMillis(), TimeUnit.MILLISECONDS)
                .maximumSize(properties.cache().maximumSize()));
        return manager;
    }
}
