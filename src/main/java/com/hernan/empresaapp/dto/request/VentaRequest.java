package com.hernan.empresaapp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cuerpo de la petición para registrar una venta completa con sus líneas.
 */
@Data
public class VentaRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    private String numeroFactura;
    private String observacion;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    @Valid
    private List<DetalleVentaRequest> detalles;

    @Data
    public static class DetalleVentaRequest {

        @NotNull(message = "El producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;

        /** Si es null, se usa el precio actual del producto */
        private BigDecimal precioUnitario;
    }
}
