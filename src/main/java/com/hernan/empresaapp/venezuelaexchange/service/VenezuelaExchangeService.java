package com.hernan.empresaapp.venezuelaexchange.service;

import com.hernan.empresaapp.venezuelaexchange.client.VenezuelaExchangeProvider;
import com.hernan.empresaapp.venezuelaexchange.dto.*;
import com.hernan.empresaapp.venezuelaexchange.entity.ExchangeRateType;
import com.hernan.empresaapp.venezuelaexchange.entity.VenezuelaExchangeRate;
import com.hernan.empresaapp.venezuelaexchange.exception.ExchangeRateUnavailableException;
import com.hernan.empresaapp.venezuelaexchange.exception.InvalidExchangeRequestException;
import com.hernan.empresaapp.venezuelaexchange.exception.VenezuelaExternalApiException;
import com.hernan.empresaapp.venezuelaexchange.repository.VenezuelaExchangeRateRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class VenezuelaExchangeService {

    private static final String VES = "VES";
    private static final Set<String> CURRENCIES = Set.of("VES", "USD", "EUR");
    private static final ExchangeRateType DEFAULT_TYPE = ExchangeRateType.OFICIAL;

    private final VenezuelaExchangeProvider provider;
    private final VenezuelaExchangeRateRepository repository;
    private final CacheManager cacheManager;

    public VenezuelaExchangeService(
            VenezuelaExchangeProvider provider,
            VenezuelaExchangeRateRepository repository,
            @Qualifier("venezuelaExchangeCacheManager") CacheManager cacheManager) {
        this.provider = provider;
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public List<ExchangeRateResponse> latest() {
        try {
            return synchronize(false).rates();
        } catch (VenezuelaExternalApiException ex) {
            List<ExchangeRateResponse> fallback = List.of("USD", "EUR").stream()
                    .map(currency -> findLatest(currency, DEFAULT_TYPE, true))
                    .flatMap(java.util.Optional::stream)
                    .toList();
            if (fallback.isEmpty()) {
                throw new ExchangeRateUnavailableException(
                        "La fuente externa no responde y todavía no existen tasas guardadas");
            }
            return fallback;
        }
    }

    @Transactional
    public ExchangeRateResponse latest(String currency) {
        String normalized = validateForeignCurrency(currency);
        try {
            synchronize(false);
            return findLatest(normalized, DEFAULT_TYPE, false)
                    .orElseThrow(() -> unavailable(normalized));
        } catch (VenezuelaExternalApiException ex) {
            return findLatest(normalized, DEFAULT_TYPE, true)
                    .orElseThrow(() -> unavailable(normalized));
        }
    }

    @Transactional(readOnly = true)
    public List<ExchangeRateResponse> history(
            String currency, LocalDate from, LocalDate to, ExchangeRateType type) {
        String normalized = validateForeignCurrency(currency);
        validateDates(from, to);
        ExchangeRateType normalizedType = type == null ? DEFAULT_TYPE : type;
        return repository
                .findByOriginCurrencyAndDestinationCurrencyAndRateTypeAndQuotationDateBetweenOrderByQuotationDateAsc(
                        normalized, VES, normalizedType, from, to)
                .stream().map(entity -> map(entity, false)).toList();
    }

    @Transactional(readOnly = true)
    public ExchangeConversionResponse convert(String from, String to, BigDecimal amount) {
        String normalizedFrom = validateCurrency(from);
        String normalizedTo = validateCurrency(to);
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidExchangeRequestException("El monto debe ser mayor que cero");
        }
        if (normalizedFrom.equals(normalizedTo)) {
            return conversion(normalizedFrom, normalizedTo, amount, BigDecimal.ONE,
                    LocalDate.now(), "IDENTITY", false);
        }

        RateFactor fromFactor = factorToVes(normalizedFrom);
        RateFactor toFactor = factorToVes(normalizedTo);
        BigDecimal rate = fromFactor.value()
                .divide(toFactor.value(), 10, RoundingMode.HALF_UP);
        boolean stale = fromFactor.stale() || toFactor.stale();
        LocalDate date = fromFactor.date().isBefore(toFactor.date())
                ? fromFactor.date() : toFactor.date();
        String source = stale ? "DATABASE_FALLBACK" : provider.source();
        return conversion(normalizedFrom, normalizedTo, amount, rate, date, source, stale);
    }

    @Transactional
    public ExchangeSyncResponse synchronize(boolean forceRefresh) {
        if (forceRefresh) {
            Cache cache = cacheManager.getCache("venezuelaLatestRemote");
            if (cache != null) cache.clear();
        }
        BcvTodayRateDto remote = provider.fetchLatest();
        List<VenezuelaExchangeRate> entities = List.of(
                upsert("USD", remote.usd(), remote.effectiveDate()),
                upsert("EUR", remote.eur(), remote.effectiveDate()));
        return new ExchangeSyncResponse(entities.size(), LocalDateTime.now(),
                entities.stream().map(entity -> map(entity, false)).toList());
    }

    private VenezuelaExchangeRate upsert(String currency, BigDecimal rate, LocalDate date) {
        VenezuelaExchangeRate entity = repository
                .findByOriginCurrencyAndDestinationCurrencyAndSourceAndRateTypeAndQuotationDate(
                        currency, VES, provider.source(), DEFAULT_TYPE, date)
                .orElseGet(VenezuelaExchangeRate::new);
        entity.setOriginCurrency(currency);
        entity.setDestinationCurrency(VES);
        entity.setRate(rate);
        entity.setSource(provider.source());
        entity.setQuotationDate(date);
        entity.setRateType(DEFAULT_TYPE);
        return repository.save(entity);
    }

    private RateFactor factorToVes(String currency) {
        if (VES.equals(currency)) {
            return new RateFactor(BigDecimal.ONE, LocalDate.now(), false);
        }
        ExchangeRateResponse latest = latest(currency);
        return new RateFactor(latest.rate(), latest.quotationDate(), latest.stale());
    }

    private java.util.Optional<ExchangeRateResponse> findLatest(
            String currency, ExchangeRateType type, boolean stale) {
        return repository
                .findFirstByOriginCurrencyAndDestinationCurrencyAndRateTypeOrderByQuotationDateDescRegisteredAtDesc(
                        currency, VES, type)
                .map(entity -> map(entity, stale));
    }

    private ExchangeConversionResponse conversion(
            String from, String to, BigDecimal amount, BigDecimal rate,
            LocalDate date, String source, boolean stale) {
        return new ExchangeConversionResponse(from, to, amount, rate,
                amount.multiply(rate).setScale(4, RoundingMode.HALF_UP),
                date, source, stale);
    }

    private ExchangeRateResponse map(VenezuelaExchangeRate entity, boolean stale) {
        return new ExchangeRateResponse(entity.getId(), entity.getOriginCurrency(),
                entity.getDestinationCurrency(), entity.getRate(), entity.getSource(),
                entity.getQuotationDate(), entity.getRegisteredAt(), entity.getRateType(), stale);
    }

    private String validateCurrency(String currency) {
        String normalized = currency == null ? "" : currency.trim().toUpperCase(Locale.ROOT);
        if (!CURRENCIES.contains(normalized)) {
            throw new InvalidExchangeRequestException(
                    "Moneda no soportada: " + currency + ". Use VES, USD o EUR");
        }
        return normalized;
    }

    private String validateForeignCurrency(String currency) {
        String normalized = validateCurrency(currency);
        if (VES.equals(normalized)) {
            throw new InvalidExchangeRequestException(
                    "Para consultar una cotización indique USD o EUR");
        }
        return normalized;
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new InvalidExchangeRequestException("Las fechas from y to son obligatorias");
        }
        if (from.isAfter(to)) {
            throw new InvalidExchangeRequestException(
                    "La fecha from no puede ser posterior a to");
        }
    }

    private ExchangeRateUnavailableException unavailable(String currency) {
        return new ExchangeRateUnavailableException(
                "No existe una tasa disponible para " + currency + "/VES");
    }

    private record RateFactor(BigDecimal value, LocalDate date, boolean stale) {}
}
