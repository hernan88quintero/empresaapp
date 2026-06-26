package com.hernan.empresaapp.dto.exchange;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeConversionResponse(
        String from,
        String to,
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal convertedAmount,
        LocalDate date,
        String source,
        boolean stale
) {
}
