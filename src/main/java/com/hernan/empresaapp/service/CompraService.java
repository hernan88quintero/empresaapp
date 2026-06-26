package com.hernan.empresaapp.service;

import com.hernan.empresaapp.dto.request.CompraRequest;
import com.hernan.empresaapp.exception.ResourceNotFoundException;
import com.hernan.empresaapp.model.Compra;
import com.hernan.empresaapp.model.DetalleCompra;
import com.hernan.empresaapp.model.Producto;
import com.hernan.empresaapp.model.Proveedor;
import com.hernan.empresaapp.repository.CompraRepository;
import com.hernan.empresaapp.repository.ProductoRepository;
import com.hernan.empresaapp.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Registra compras a proveedores y aumenta el inventario automáticamente.
 */
@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    public CompraService(
            CompraRepository compraRepository,
            ProveedorRepository proveedorRepository,
            ProductoRepository productoRepository,
            InventarioService inventarioService) {
        this.compraRepository = compraRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }

    @Transactional(readOnly = true)
    public List<Compra> listar() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Compra buscarPorId(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
    }

    /**
     * Crea la compra, calcula totales y suma stock por cada línea de detalle.
     */
    @Transactional
    public Compra registrar(CompraRequest request) {
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.getProveedorId()));

        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setObservacion(request.getObservacion());

        BigDecimal total = BigDecimal.ZERO;

        for (CompraRequest.DetalleCompraRequest linea : request.getDetalles()) {
            Producto producto = productoRepository.findById(linea.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", linea.getProductoId()));

            BigDecimal subtotal = linea.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(linea.getCantidad()));

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(linea.getCantidad());
            detalle.setPrecioUnitario(linea.getPrecioUnitario());
            detalle.setSubtotal(subtotal);
            compra.getDetalles().add(detalle);

            total = total.add(subtotal);

            inventarioService.registrarEntrada(
                    producto,
                    linea.getCantidad(),
                    "Compra #" + (request.getNumeroFactura() != null ? request.getNumeroFactura() : "nueva"));
        }

        compra.setTotal(total);
        return compraRepository.save(compra);
    }
}
