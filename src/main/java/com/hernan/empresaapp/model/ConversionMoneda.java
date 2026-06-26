package com.hernan.empresaapp.model;

import com.hernan.empresaapp.model.enums.Moneda;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversiones_moneda")
@Data
public class ConversionMoneda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda_origen", nullable = false, length = 3, columnDefinition = "varchar(3)")
    private Moneda monedaOrigen;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda_destino", nullable = false, length = 3, columnDefinition = "varchar(3)")
    private Moneda monedaDestino;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal monto;

    @Column(name = "tipo_cambio", nullable = false, precision = 19, scale = 8)
    private BigDecimal tipoCambio;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal resultado;

    @Column(name = "fecha_conversion", nullable = false)
    private LocalDateTime fechaConversion = LocalDateTime.now();
}
