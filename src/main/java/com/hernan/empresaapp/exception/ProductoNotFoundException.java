package com.hernan.empresaapp.exception;

/**
 * Mantiene compatibilidad con el código anterior de productos.
 */
public class ProductoNotFoundException extends ResourceNotFoundException {

    public ProductoNotFoundException(Long id) {
        super("Producto", id);
    }
}
