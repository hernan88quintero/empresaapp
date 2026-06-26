package com.hernan.empresaapp.service;

import com.hernan.empresaapp.dto.request.ConversionMonedaRequest;
import com.hernan.empresaapp.dto.response.CotizacionesResponse;
import com.hernan.empresaapp.exception.BusinessException;
import com.hernan.empresaapp.model.ConversionMoneda;
import com.hernan.empresaapp.model.enums.Moneda;
import com.hernan.empresaapp.repository.ConversionMonedaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversionMonedaService {

    private final ConversionMonedaRepository repository;
    private final ExchangeRateService exchangeRateService;

    public ConversionMonedaService(
            ConversionMonedaRepository repository,
            ExchangeRateService exchangeRateService) {
        this.repository = repository;
        this.exchangeRateService = exchangeRateService;
    }

    public CotizacionesResponse obtenerCotizaciones() {
        var latest = exchangeRateService.latest("ARS");
        return new CotizacionesResponse("ARS", latest.rates(), latest.date().atStartOfDay());
    }

    public ConversionMoneda calcular(ConversionMonedaRequest request) {
        if (request.monedaOrigen() == request.monedaDestino()) {
            throw new BusinessException("Las monedas de origen y destino deben ser diferentes");
        }

        var live = exchangeRateService.convert(
                request.monedaOrigen().name(),
                request.monedaDestino().name(),
                request.monto());

        ConversionMoneda conversion = new ConversionMoneda();
        conversion.setMonedaOrigen(request.monedaOrigen());
        conversion.setMonedaDestino(request.monedaDestino());
        conversion.setMonto(live.amount());
        conversion.setTipoCambio(live.rate());
        conversion.setResultado(live.convertedAmount());
        return conversion;
    }

    public ConversionMoneda convertir(ConversionMonedaRequest request) {
        return repository.save(calcular(request));
    }

    public List<ConversionMoneda> listar() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "fechaConversion"));
    }
}
