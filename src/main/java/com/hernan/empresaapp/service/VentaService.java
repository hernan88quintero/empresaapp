package com.hernan.empresaapp.service;

import com.hernan.empresaapp.dto.request.VentaRequest;
import com.hernan.empresaapp.exception.ResourceNotFoundException;
import com.hernan.empresaapp.model.Cliente;
import com.hernan.empresaapp.model.DetalleVenta;
import com.hernan.empresaapp.model.Producto;
import com.hernan.empresaapp.model.Venta;
import com.hernan.empresaapp.repository.ClienteRepository;
import com.hernan.empresaapp.repository.ProductoRepository;
import com.hernan.empresaapp.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Registra ventas a clientes y descuenta stock del inventario.
 */
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    public VentaService(
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            InventarioService inventarioService) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }

    @Transactional(readOnly = true)
    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
    }

    @Transactional
    public Venta registrar(VentaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClienteId()));

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setNumeroFactura(request.getNumeroFactura());
        venta.setObservacion(request.getObservacion());

        BigDecimal total = BigDecimal.ZERO;

        for (VentaRequest.DetalleVentaRequest linea : request.getDetalles()) {
            Producto producto = productoRepository.findById(linea.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", linea.getProductoId()));

            BigDecimal precioUnitario = linea.getPrecioUnitario() != null
                    ? linea.getPrecioUnitario()
                    : producto.getPrecio();

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(linea.getCantidad()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(linea.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            venta.getDetalles().add(detalle);

            total = total.add(subtotal);

            inventarioService.registrarSalida(
                    producto,
                    linea.getCantidad(),
                    "Venta #" + (request.getNumeroFactura() != null ? request.getNumeroFactura() : "nueva"));
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }
}
