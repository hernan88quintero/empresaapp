package com.hernan.empresaapp.venezuelaexchange.service;

import com.hernan.empresaapp.venezuelaexchange.exception.VenezuelaExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "venezuela-exchange.scheduler", name = "enabled",
        havingValue = "true", matchIfMissing = true)
public class VenezuelaExchangeScheduler {

    private static final Logger log = LoggerFactory.getLogger(VenezuelaExchangeScheduler.class);
    private final VenezuelaExchangeService service;

    public VenezuelaExchangeScheduler(VenezuelaExchangeService service) {
        this.service = service;
    }

    @Scheduled(cron = "${venezuela-exchange.scheduler.cron}",
            zone = "${venezuela-exchange.scheduler.zone}")
    public void synchronizeDailyRates() {
        try {
            service.synchronize(true);
            log.info("Tasas oficiales venezolanas sincronizadas");
        } catch (VenezuelaExternalApiException ex) {
            log.warn("No se pudieron sincronizar las tasas venezolanas: {}", ex.getMessage());
        }
    }
}
