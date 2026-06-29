package com.hernan.empresaapp.venezuelaexchange.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "venezuela_exchange_rates",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ve_rate_pair_source_type_date",
                columnNames = {"origin_currency", "destination_currency", "source", "rate_type", "quotation_date"}),
        indexes = {
                @Index(name = "idx_ve_rate_pair_date",
                        columnList = "origin_currency,destination_currency,quotation_date"),
                @Index(name = "idx_ve_rate_quotation_date", columnList = "quotation_date")
        })
@Getter
@Setter
@NoArgsConstructor
public class VenezuelaExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin_currency", nullable = false, length = 3)
    private String originCurrency;

    @Column(name = "destination_currency", nullable = false, length = 3)
    private String destinationCurrency;

    @Column(nullable = false, precision = 24, scale = 10)
    private BigDecimal rate;

    @Column(nullable = false, length = 60)
    private String source;

    @Column(name = "quotation_date", nullable = false)
    private LocalDate quotationDate;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false, length = 20)
    private ExchangeRateType rateType;

    @PrePersist
    void prePersist() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }
}
