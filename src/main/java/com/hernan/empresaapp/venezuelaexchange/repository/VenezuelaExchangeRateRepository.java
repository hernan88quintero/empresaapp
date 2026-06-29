package com.hernan.empresaapp.venezuelaexchange.repository;

import com.hernan.empresaapp.venezuelaexchange.entity.ExchangeRateType;
import com.hernan.empresaapp.venezuelaexchange.entity.VenezuelaExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VenezuelaExchangeRateRepository extends JpaRepository<VenezuelaExchangeRate, Long> {

    Optional<VenezuelaExchangeRate>
    findFirstByOriginCurrencyAndDestinationCurrencyAndRateTypeOrderByQuotationDateDescRegisteredAtDesc(
            String originCurrency, String destinationCurrency, ExchangeRateType rateType);

    List<VenezuelaExchangeRate>
    findByOriginCurrencyAndDestinationCurrencyAndRateTypeAndQuotationDateBetweenOrderByQuotationDateAsc(
            String originCurrency, String destinationCurrency, ExchangeRateType rateType,
            LocalDate from, LocalDate to);

    Optional<VenezuelaExchangeRate>
    findByOriginCurrencyAndDestinationCurrencyAndSourceAndRateTypeAndQuotationDate(
            String originCurrency, String destinationCurrency, String source,
            ExchangeRateType rateType, LocalDate quotationDate);
}
