package com.hernan.empresaapp.venezuelaexchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record BcvTodayRateDto(
        @JsonProperty("USD") BigDecimal usd,
        @JsonProperty("EUR") BigDecimal eur,
        @JsonProperty("updated_at") OffsetDateTime updatedAt,
        @JsonProperty("effective_date") LocalDate effectiveDate,
        LocalDate date) {
}
