package com.hernan.empresaapp.controller;

import com.hernan.empresaapp.dto.request.VentaRequest;
import com.hernan.empresaapp.model.Venta;
import com.hernan.empresaapp.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    public Venta buscarPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id);
    }

    /** Registra venta y descuenta stock automáticamente */
    @PostMapping
    public Venta registrar(@Valid @RequestBody VentaRequest request) {
        return ventaService.registrar(request);
    }
}
