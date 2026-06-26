package com.hernan.empresaapp.service;

import com.hernan.empresaapp.dto.exchange.FrankfurterRateDTO;
import com.hernan.empresaapp.exception.ExternalExchangeServiceException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class FrankfurterExchangeClient {

    private static final ParameterizedTypeReference<List<FrankfurterRateDTO>> RATE_LIST =
            new ParameterizedTypeReference<>() {};

    private final WebClient webClient;

    public FrankfurterExchangeClient(WebClient exchangeWebClient) {
        this.webClient = exchangeWebClient;
    }

    @Cacheable(cacheNames = "exchangeProviderLatest", key = "#base")
    public List<FrankfurterRateDTO> latest(String base) {
        return execute(() -> webClient.get()
                .uri(uri -> uri.path("/rates").queryParam("base", base).build())
                .retrieve()
                .bodyToMono(RATE_LIST)
                .block(), "No se pudieron obtener las cotizaciones actuales");
    }

    @Cacheable(cacheNames = "exchangePair", key = "#from + ':' + #to")
    public FrankfurterRateDTO rate(String from, String to) {
        return execute(() -> webClient.get()
                .uri("/rate/{from}/{to}", from, to)
                .retrieve()
                .bodyToMono(FrankfurterRateDTO.class)
                .block(), "No se pudo obtener el tipo de cambio solicitado");
    }

    private <T> T execute(RemoteCall<T> call, String message) {
        try {
            T response = call.run();
            if (response == null) {
                throw new ExternalExchangeServiceException(message);
            }
            return response;
        } catch (ExternalExchangeServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExternalExchangeServiceException(message);
        }
    }

    @FunctionalInterface
    private interface RemoteCall<T> {
        T run();
    }
}
