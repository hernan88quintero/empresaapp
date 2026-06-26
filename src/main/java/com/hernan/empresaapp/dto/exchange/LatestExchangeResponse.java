package com.hernan.empresaapp.dto.exchange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record LatestExchangeResponse(
        LocalDate date,
        String base,
        Map<String, BigDecimal> rates,
        String source,
        boolean stale
) {
}
