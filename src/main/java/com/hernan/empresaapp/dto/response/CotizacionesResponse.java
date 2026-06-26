package com.hernan.empresaapp.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record CotizacionesResponse(
        String monedaBase,
        Map<String, BigDecimal> valoresEnArs,
        LocalDateTime fechaActualizacion
) {
}
