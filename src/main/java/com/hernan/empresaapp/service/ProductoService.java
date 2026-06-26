package com.hernan.empresaapp.service;

import com.hernan.empresaapp.exception.ProductoNotFoundException;
import com.hernan.empresaapp.model.Producto;
import com.hernan.empresaapp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Casos de uso del catálogo de productos (inventario base).
 * El stock se modifica principalmente vía CompraService y VentaService.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto buscarProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    public Producto crearProducto(Producto producto) {
        if (producto.getStock() == null) {
            producto.setStock(0);
        }
        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(5);
        }
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        Producto productoExistente = buscarProductoPorId(id);

        productoExistente.setCodigo(productoActualizado.getCodigo());
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setStock(productoActualizado.getStock());
        productoExistente.setStockMinimo(productoActualizado.getStockMinimo());
        productoExistente.setPrecio(productoActualizado.getPrecio());

        return productoRepository.save(productoExistente);
    }

    public void eliminarProducto(Long id) {
        Producto productoExistente = buscarProductoPorId(id);
        productoRepository.delete(productoExistente);
    }
}
