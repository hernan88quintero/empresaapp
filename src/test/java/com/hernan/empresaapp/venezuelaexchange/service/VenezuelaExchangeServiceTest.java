package com.hernan.empresaapp.venezuelaexchange.service;

import com.hernan.empresaapp.venezuelaexchange.client.VenezuelaExchangeProvider;
import com.hernan.empresaapp.venezuelaexchange.dto.BcvTodayRateDto;
import com.hernan.empresaapp.venezuelaexchange.dto.ExchangeConversionResponse;
import com.hernan.empresaapp.venezuelaexchange.dto.ExchangeRateResponse;
import com.hernan.empresaapp.venezuelaexchange.entity.ExchangeRateType;
import com.hernan.empresaapp.venezuelaexchange.entity.VenezuelaExchangeRate;
import com.hernan.empresaapp.venezuelaexchange.exception.VenezuelaExternalApiException;
import com.hernan.empresaapp.venezuelaexchange.repository.VenezuelaExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VenezuelaExchangeServiceTest {

    private VenezuelaExchangeProvider provider;
    private VenezuelaExchangeRateRepository repository;
    private VenezuelaExchangeService service;

    @BeforeEach
    void setUp() {
        provider = mock(VenezuelaExchangeProvider.class);
        repository = mock(VenezuelaExchangeRateRepository.class);
        CacheManager cacheManager = new ConcurrentMapCacheManager("venezuelaLatestRemote");
        service = new VenezuelaExchangeService(provider, repository, cacheManager);
        when(provider.source()).thenReturn("BCV_TODAY");
        when(repository.save(any(VenezuelaExchangeRate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void synchronizesUsdAndEur() {
        when(provider.fetchLatest()).thenReturn(new BcvTodayRateDto(
                new BigDecimal("622.2135"), new BigDecimal("708.3901"),
                null, LocalDate.of(2026, 6, 29), LocalDate.of(2026, 6, 29)));
        when(repository
                .findByOriginCurrencyAndDestinationCurrencyAndSourceAndRateTypeAndQuotationDate(
                        anyString(), eq("VES"), eq("BCV_TODAY"), eq(ExchangeRateType.OFICIAL),
                        any(LocalDate.class)))
                .thenReturn(Optional.empty());

        var result = service.synchronize(false);

        assertThat(result.synchronizedRates()).isEqualTo(2);
        assertThat(result.rates()).extracting(ExchangeRateResponse::originCurrency)
                .containsExactly("USD", "EUR");
        verify(repository, times(2)).save(any(VenezuelaExchangeRate.class));
    }

    @Test
    void usesDatabaseFallbackWhenProviderFails() {
        when(provider.fetchLatest()).thenThrow(
                new VenezuelaExternalApiException("Proveedor caído", null));
        VenezuelaExchangeRate saved = rate("USD", "622.2135");
        when(repository
                .findFirstByOriginCurrencyAndDestinationCurrencyAndRateTypeOrderByQuotationDateDescRegisteredAtDesc(
                        "USD", "VES", ExchangeRateType.OFICIAL))
                .thenReturn(Optional.of(saved));

        ExchangeRateResponse response = service.latest("USD");

        assertThat(response.stale()).isTrue();
        assertThat(response.rate()).isEqualByComparingTo("622.2135");
    }

    @Test
    void convertsUsdToEurUsingVesAsPivot() {
        when(provider.fetchLatest()).thenReturn(new BcvTodayRateDto(
                new BigDecimal("600"), new BigDecimal("720"),
                null, LocalDate.of(2026, 6, 29), LocalDate.of(2026, 6, 29)));
        when(repository
                .findByOriginCurrencyAndDestinationCurrencyAndSourceAndRateTypeAndQuotationDate(
                        anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(Optional.empty());
        when(repository
                .findFirstByOriginCurrencyAndDestinationCurrencyAndRateTypeOrderByQuotationDateDescRegisteredAtDesc(
                        eq("USD"), eq("VES"), eq(ExchangeRateType.OFICIAL)))
                .thenReturn(Optional.of(rate("USD", "600")));
        when(repository
                .findFirstByOriginCurrencyAndDestinationCurrencyAndRateTypeOrderByQuotationDateDescRegisteredAtDesc(
                        eq("EUR"), eq("VES"), eq(ExchangeRateType.OFICIAL)))
                .thenReturn(Optional.of(rate("EUR", "720")));

        ExchangeConversionResponse response =
                service.convert("USD", "EUR", new BigDecimal("100"));

        assertThat(response.convertedAmount()).isEqualByComparingTo("83.3333");
    }

    private VenezuelaExchangeRate rate(String currency, String value) {
        VenezuelaExchangeRate entity = new VenezuelaExchangeRate();
        entity.setOriginCurrency(currency);
        entity.setDestinationCurrency("VES");
        entity.setRate(new BigDecimal(value));
        entity.setSource("BCV_TODAY");
        entity.setRateType(ExchangeRateType.OFICIAL);
        entity.setQuotationDate(LocalDate.of(2026, 6, 29));
        return entity;
    }
}
