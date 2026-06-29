package com.hernan.empresaapp.venezuelaexchange.controller;

import com.hernan.empresaapp.venezuelaexchange.dto.ExchangeConversionResponse;
import com.hernan.empresaapp.venezuelaexchange.dto.ExchangeRateResponse;
import com.hernan.empresaapp.venezuelaexchange.dto.ExchangeSyncResponse;
import com.hernan.empresaapp.venezuelaexchange.entity.ExchangeRateType;
import com.hernan.empresaapp.venezuelaexchange.service.VenezuelaExchangeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/venezuela/exchange")
public class VenezuelaExchangeController {

    private final VenezuelaExchangeService service;

    public VenezuelaExchangeController(VenezuelaExchangeService service) {
        this.service = service;
    }

    @GetMapping("/latest")
    public List<ExchangeRateResponse> latest() {
        return service.latest();
    }

    @GetMapping("/latest/{currency}")
    public ExchangeRateResponse latest(@PathVariable String currency) {
        return service.latest(currency);
    }

    @GetMapping("/history")
    public List<ExchangeRateResponse> history(
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) ExchangeRateType type) {
        return service.history(currency, from, to, type);
    }

    @GetMapping("/convert")
    public ExchangeConversionResponse convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        return service.convert(from, to, amount);
    }

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.OK)
    public ExchangeSyncResponse synchronize() {
        return service.synchronize(true);
    }
}
