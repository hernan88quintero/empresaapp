package com.hernan.empresaapp.service;

import com.hernan.empresaapp.config.ExchangeProperties;
import com.hernan.empresaapp.dto.exchange.ExchangeConversionResponse;
import com.hernan.empresaapp.dto.exchange.FrankfurterRateDTO;
import com.hernan.empresaapp.dto.exchange.LatestExchangeResponse;
import com.hernan.empresaapp.exception.BusinessException;
import com.hernan.empresaapp.exception.ExternalExchangeServiceException;
import com.hernan.empresaapp.model.ExchangeRateSnapshot;
import com.hernan.empresaapp.repository.ExchangeRateSnapshotRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {

    private static final String SOURCE = "FRANKFURTER";

    private final FrankfurterExchangeClient client;
    private final ExchangeRateSnapshotRepository repository;
    private final Set<String> supportedCurrencies;

    public ExchangeRateService(
            FrankfurterExchangeClient client,
            ExchangeRateSnapshotRepository repository,
            ExchangeProperties properties) {
        this.client = client;
        this.repository = repository;
        this.supportedCurrencies = properties.supportedCurrencies().stream()
                .map(value -> value.toUpperCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> supportedCurrencies() {
        return supportedCurrencies;
    }

    @Cacheable(cacheNames = "exchangeLatest", key = "#base")
    @Transactional
    public LatestExchangeResponse latest(String base) {
        String normalizedBase = validateCurrency(base);
        try {
            List<FrankfurterRateDTO> remoteRates = client.latest(normalizedBase).stream()
                    .filter(rate -> supportedCurrencies.contains(rate.quote()))
                    .toList();
            if (remoteRates.isEmpty()) {
                throw new ExternalExchangeServiceException("El proveedor no devolvió cotizaciones");
            }
            saveSnapshots(remoteRates);
            return toLatestResponse(normalizedBase, remoteRates, SOURCE, false);
        } catch (ExternalExchangeServiceException ex) {
            return latestFallback(normalizedBase)
                    .orElseThrow(() -> ex);
        }
    }

    @Transactional
    public ExchangeConversionResponse convert(String from, String to, BigDecimal amount) {
        String normalizedFrom = validateCurrency(from);
        String normalizedTo = validateCurrency(to);
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessException("El importe debe ser mayor que cero");
        }
        if (normalizedFrom.equals(normalizedTo)) {
            return conversion(normalizedFrom, normalizedTo, amount, BigDecimal.ONE,
                    LocalDate.now(), "IDENTITY", false);
        }

        LatestExchangeResponse matrix = latest("ARS");
        BigDecimal fromPerArs = matrix.rates().get(normalizedFrom);
        BigDecimal toPerArs = matrix.rates().get(normalizedTo);
        if (fromPerArs == null || toPerArs == null) {
            throw new BusinessException("No hay cotización disponible para el par solicitado");
        }
        BigDecimal rate = toPerArs.divide(fromPerArs, 8, RoundingMode.HALF_UP);
        return conversion(normalizedFrom, normalizedTo, amount, rate,
                matrix.date(), matrix.source(), matrix.stale());
    }

    @Transactional(readOnly = true)
    public List<ExchangeRateSnapshot> history(
            String base, String quote, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new BusinessException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return repository.findByBaseCurrencyAndQuoteCurrencyAndRateDateBetweenOrderByRateDate(
                validateCurrency(base), validateCurrency(quote), from, to);
    }

    private java.util.Optional<LatestExchangeResponse> latestFallback(String base) {
        return repository.findFirstByBaseCurrencyOrderByRateDateDesc(base)
                .flatMap(reference -> {
                    List<ExchangeRateSnapshot> snapshots =
                            repository.findByBaseCurrencyAndRateDateOrderByQuoteCurrency(base, reference.getRateDate());
                    if (snapshots.isEmpty()) return java.util.Optional.empty();
                    Map<String, BigDecimal> rates = new LinkedHashMap<>();
                    rates.put(base, BigDecimal.ONE);
                    snapshots.forEach(item -> rates.put(item.getQuoteCurrency(), item.getRate()));
                    return java.util.Optional.of(new LatestExchangeResponse(
                            reference.getRateDate(), base, rates, "DATABASE_FALLBACK", true));
                });
    }

    private LatestExchangeResponse toLatestResponse(
            String base, List<FrankfurterRateDTO> values, String source, boolean stale) {
        Map<String, BigDecimal> rates = new LinkedHashMap<>();
        rates.put(base, BigDecimal.ONE);
        values.forEach(value -> rates.put(value.quote(), value.rate()));
        return new LatestExchangeResponse(values.getFirst().date(), base, rates, source, stale);
    }

    private ExchangeConversionResponse conversion(
            String from, String to, BigDecimal amount, BigDecimal rate,
            LocalDate date, String source, boolean stale) {
        return new ExchangeConversionResponse(
                from, to, amount, rate,
                amount.multiply(rate).setScale(4, RoundingMode.HALF_UP),
                date, source, stale);
    }

    private String validateCurrency(String currency) {
        if (currency == null || !supportedCurrencies.contains(currency.toUpperCase(Locale.ROOT))) {
            throw new BusinessException("Moneda no soportada: " + currency);
        }
        return currency.toUpperCase(Locale.ROOT);
    }

    private void saveSnapshots(List<FrankfurterRateDTO> rates) {
        rates.forEach(rate -> {
            if (!repository.existsByRateDateAndBaseCurrencyAndQuoteCurrency(
                    rate.date(), rate.base(), rate.quote())) {
                ExchangeRateSnapshot snapshot = new ExchangeRateSnapshot();
                snapshot.setRateDate(rate.date());
                snapshot.setBaseCurrency(rate.base());
                snapshot.setQuoteCurrency(rate.quote());
                snapshot.setRate(rate.rate());
                snapshot.setSource(SOURCE);
                repository.save(snapshot);
            }
        });
    }
}
