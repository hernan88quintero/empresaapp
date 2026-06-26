package com.hernan.empresaapp.controller;

import com.hernan.empresaapp.dto.request.ConversionMonedaRequest;
import com.hernan.empresaapp.dto.response.CotizacionesResponse;
import com.hernan.empresaapp.model.ConversionMoneda;
import com.hernan.empresaapp.service.ConversionMonedaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversiones")
public class ConversionMonedaController {

    private final ConversionMonedaService service;

    public ConversionMonedaController(ConversionMonedaService service) {
        this.service = service;
    }

    @GetMapping("/cotizaciones")
    public CotizacionesResponse obtenerCotizaciones() {
        return service.obtenerCotizaciones();
    }

    @PostMapping("/calcular")
    public ConversionMoneda calcular(@Valid @RequestBody ConversionMonedaRequest request) {
        return service.calcular(request);
    }

    @PostMapping
    public ConversionMoneda convertir(@Valid @RequestBody ConversionMonedaRequest request) {
        return service.convertir(request);
    }

    @GetMapping
    public List<ConversionMoneda> listar() {
        return service.listar();
    }
}
