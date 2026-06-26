package com.hernan.empresaapp.service;

import com.hernan.empresaapp.exception.BusinessException;
import com.hernan.empresaapp.model.MovimientoInventario;
import com.hernan.empresaapp.model.Producto;
import com.hernan.empresaapp.model.enums.TipoMovimiento;
import com.hernan.empresaapp.repository.MovimientoInventarioRepository;
import com.hernan.empresaapp.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Operaciones de inventario: movimientos de stock y alertas de stock bajo.
 * Usado internamente por compras y ventas.
 */
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public InventarioService(
            ProductoRepository productoRepository,
            MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /** Productos con stock igual o menor al mínimo configurado */
    public List<Producto> listarStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarMovimientosPorProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId);
    }

    /**
     * Aumenta stock (compras). Registra movimiento ENTRADA.
     */
    @Transactional
    public void registrarEntrada(Producto producto, int cantidad, String observacion) {
        int nuevoStock = producto.getStock() + cantidad;
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
        guardarMovimiento(producto, TipoMovimiento.ENTRADA, cantidad, nuevoStock, observacion);
    }

    /**
     * Disminuye stock (ventas). Lanza error si no hay suficiente cantidad.
     */
    @Transactional
    public void registrarSalida(Producto producto, int cantidad, String observacion) {
        if (producto.getStock() < cantidad) {
            throw new BusinessException(
                    "Stock insuficiente para " + producto.getNombre() +
                            ". Disponible: " + producto.getStock() + ", solicitado: " + cantidad);
        }
        int nuevoStock = producto.getStock() - cantidad;
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
        guardarMovimiento(producto, TipoMovimiento.SALIDA, cantidad, nuevoStock, observacion);
    }

    @Transactional
    public void registrarAjuste(Producto producto, int cantidad, String observacion) {
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new BusinessException("El ajuste dejaría stock negativo en " + producto.getNombre());
        }
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
        guardarMovimiento(producto, TipoMovimiento.AJUSTE, Math.abs(cantidad), nuevoStock, observacion);
    }

    private void guardarMovimiento(
            Producto producto,
            TipoMovimiento tipo,
            int cantidad,
            int stockResultante,
            String observacion) {

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockResultante(stockResultante);
        movimiento.setObservacion(observacion);
        movimientoRepository.save(movimiento);
    }
}
