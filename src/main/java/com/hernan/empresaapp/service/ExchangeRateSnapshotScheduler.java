package com.hernan.empresaapp.service;

import com.hernan.empresaapp.config.ExchangeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateSnapshotScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateSnapshotScheduler.class);

    private final ExchangeRateService service;
    private final ExchangeProperties properties;

    public ExchangeRateSnapshotScheduler(ExchangeRateService service, ExchangeProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @Scheduled(cron = "${exchange.scheduler.cron}", zone = "${exchange.scheduler.zone}")
    public void storeDailyRates() {
        if (!properties.scheduler().enabled()) return;
        try {
            service.latest("USD");
            log.info("Cotizaciones diarias actualizadas correctamente");
        } catch (Exception ex) {
            log.warn("No fue posible actualizar las cotizaciones diarias: {}", ex.getMessage());
        }
    }
}
