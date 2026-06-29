package com.hernan.empresaapp.venezuelaexchange.client;

import com.hernan.empresaapp.venezuelaexchange.dto.BcvTodayRateDto;
import com.hernan.empresaapp.venezuelaexchange.exception.VenezuelaExternalApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BcvTodayClient implements VenezuelaExchangeProvider {

    public static final String SOURCE = "BCV_TODAY";
    private final WebClient webClient;

    public BcvTodayClient(@Qualifier("venezuelaExchangeWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    @Cacheable(cacheNames = "venezuelaLatestRemote",
            cacheManager = "venezuelaExchangeCacheManager",
            key = "'official'")
    public BcvTodayRateDto fetchLatest() {
        try {
            BcvTodayRateDto response = webClient.get()
                    .uri("/api/v1/rate.json")
                    .retrieve()
                    .bodyToMono(BcvTodayRateDto.class)
                    .block();
            if (response == null || response.usd() == null || response.eur() == null
                    || response.effectiveDate() == null) {
                throw new VenezuelaExternalApiException(
                        "BCV Today devolvió una respuesta incompleta", null);
            }
            return response;
        } catch (VenezuelaExternalApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new VenezuelaExternalApiException(
                    "No fue posible consultar las tasas oficiales de Venezuela", ex);
        }
    }

    @Override
    public String source() {
        return SOURCE;
    }
}
