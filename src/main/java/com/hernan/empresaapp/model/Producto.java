package com.hernan.empresaapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Producto del catálogo / inventario.
 * El campo stock se actualiza automáticamente al registrar compras y ventas.
 */
@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código interno o de barras del producto */
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    /** Cantidad disponible en almacén */
    @Column(nullable = false)
    private Integer stock = 0;

    /** Stock mínimo para alertas de reposición */
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
