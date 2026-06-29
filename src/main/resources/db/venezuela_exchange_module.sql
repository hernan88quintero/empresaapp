CREATE TABLE IF NOT EXISTS venezuela_exchange_rates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    origin_currency VARCHAR(3) NOT NULL,
    destination_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(24,10) NOT NULL,
    source VARCHAR(60) NOT NULL,
    quotation_date DATE NOT NULL,
    registered_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    rate_type VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_ve_rate_pair_source_type_date
        UNIQUE (origin_currency, destination_currency, source, rate_type, quotation_date),
    INDEX idx_ve_rate_pair_date
        (origin_currency, destination_currency, quotation_date),
    INDEX idx_ve_rate_quotation_date (quotation_date),
    CONSTRAINT chk_ve_rate_positive CHECK (rate > 0),
    CONSTRAINT chk_ve_rate_currency CHECK (
        origin_currency IN ('USD', 'EUR', 'VES')
        AND destination_currency IN ('USD', 'EUR', 'VES')
    ),
    CONSTRAINT chk_ve_rate_type CHECK (
        rate_type IN ('OFICIAL', 'PROMEDIO', 'PARALELO')
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
