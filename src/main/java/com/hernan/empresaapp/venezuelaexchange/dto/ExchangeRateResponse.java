package com.hernan.empresaapp.venezuelaexchange.dto;

import com.hernan.empresaapp.venezuelaexchange.entity.ExchangeRateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExchangeRateResponse(
        Long id,
        String originCurrency,
        String destinationCurrency,
        BigDecimal rate,
        String source,
        LocalDate quotationDate,
        LocalDateTime registeredAt,
        ExchangeRateType rateType,
        boolean stale) {
}
