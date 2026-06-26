package com.hernan.empresaapp.dto.exchange;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FrankfurterRateDTO(
        LocalDate date,
        String base,
        String quote,
        BigDecimal rate
) {
}
