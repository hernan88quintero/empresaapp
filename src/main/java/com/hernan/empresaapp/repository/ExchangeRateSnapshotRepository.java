package com.hernan.empresaapp.repository;

import com.hernan.empresaapp.model.ExchangeRateSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateSnapshotRepository extends JpaRepository<ExchangeRateSnapshot, Long> {

    Optional<ExchangeRateSnapshot> findFirstByBaseCurrencyAndQuoteCurrencyOrderByRateDateDesc(
            String baseCurrency, String quoteCurrency);

    Optional<ExchangeRateSnapshot> findFirstByBaseCurrencyOrderByRateDateDesc(String baseCurrency);

    List<ExchangeRateSnapshot> findByBaseCurrencyAndRateDateOrderByQuoteCurrency(
            String baseCurrency, LocalDate rateDate);

    List<ExchangeRateSnapshot> findByBaseCurrencyAndQuoteCurrencyAndRateDateBetweenOrderByRateDate(
            String baseCurrency, String quoteCurrency, LocalDate from, LocalDate to);

    boolean existsByRateDateAndBaseCurrencyAndQuoteCurrency(
            LocalDate rateDate, String baseCurrency, String quoteCurrency);
}
