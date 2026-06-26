package com.hernan.empresaapp.controller;

import com.hernan.empresaapp.dto.exchange.ExchangeConversionResponse;
import com.hernan.empresaapp.dto.exchange.LatestExchangeResponse;
import com.hernan.empresaapp.model.ExchangeRateSnapshot;
import com.hernan.empresaapp.service.ExchangeRateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @GetMapping("/latest")
    public LatestExchangeResponse latest() {
        return service.latest("USD");
    }

    @GetMapping("/latest/{base}")
    public LatestExchangeResponse latest(@PathVariable String base) {
        return service.latest(base);
    }

    @GetMapping("/convert")
    public ExchangeConversionResponse convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        return service.convert(from, to, amount);
    }

    @GetMapping("/currencies")
    public Set<String> currencies() {
        return service.supportedCurrencies();
    }

    @GetMapping("/history")
    public List<ExchangeRateSnapshot> history(
            @RequestParam String base,
            @RequestParam String quote,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.history(base, quote, from, to);
    }
}
