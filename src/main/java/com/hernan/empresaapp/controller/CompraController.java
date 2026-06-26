package com.hernan.empresaapp.controller;

import com.hernan.empresaapp.dto.request.CompraRequest;
import com.hernan.empresaapp.model.Compra;
import com.hernan.empresaapp.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<Compra> listar() {
        return compraService.listar();
    }

    @GetMapping("/{id}")
    public Compra buscarPorId(@PathVariable Long id) {
        return compraService.buscarPorId(id);
    }

    /** Registra compra y aumenta stock automáticamente */
    @PostMapping
    public Compra registrar(@Valid @RequestBody CompraRequest request) {
        return compraService.registrar(request);
    }
}
