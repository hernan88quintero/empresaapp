package com.hernan.empresaapp.dto.request;

import com.hernan.empresaapp.model.enums.Moneda;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionMonedaRequest(
        @NotNull(message = "La moneda de origen es obligatoria")
        Moneda monedaOrigen,
        @NotNull(message = "La moneda de destino es obligatoria")
        Moneda monedaDestino,
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
        BigDecimal monto
) {
}
