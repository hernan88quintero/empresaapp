package com.hernan.empresaapp.controller;

import com.hernan.empresaapp.model.MovimientoInventario;
import com.hernan.empresaapp.model.Producto;
import com.hernan.empresaapp.service.InventarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Consultas de inventario: stock bajo y historial de movimientos.
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    /** Productos que necesitan reposición */
    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {
        return inventarioService.listarStockBajo();
    }

    @GetMapping("/movimientos")
    public List<MovimientoInventario> movimientos() {
        return inventarioService.listarMovimientos();
    }

    @GetMapping("/movimientos/producto/{productoId}")
    public List<MovimientoInventario> movimientosPorProducto(@PathVariable Long productoId) {
        return inventarioService.listarMovimientosPorProducto(productoId);
    }
}
