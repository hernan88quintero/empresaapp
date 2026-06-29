package com.hernan.empresaapp.venezuelaexchange.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeConversionResponse(
        String from,
        String to,
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal convertedAmount,
        LocalDate quotationDate,
        String source,
        boolean stale) {
}
