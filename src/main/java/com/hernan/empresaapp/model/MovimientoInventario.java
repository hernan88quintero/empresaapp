package com.hernan.empresaapp.model;

import com.hernan.empresaapp.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Historial de cambios de stock.
 * Cada compra, venta o ajuste genera un movimiento para auditoría.
 */
@Entity
@Table(name = "movimientos_inventario")
@Data
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    /** Cantidad del movimiento (siempre positiva; el tipo indica entrada o salida) */
    @Column(nullable = false)
    private Integer cantidad;

    /** Stock resultante después del movimiento */
    @Column(name = "stock_resultante", nullable = false)
    private Integer stockResultante;

    @Column(length = 255)
    private String observacion;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento = LocalDateTime.now();
}
