package com.hernan.empresaapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "exchange_rate_snapshots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_exchange_rate_day_pair",
                columnNames = {"rate_date", "base_currency", "quote_currency"}))
@Data
public class ExchangeRateSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @Column(name = "quote_currency", nullable = false, length = 3)
    private String quoteCurrency;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal rate;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
