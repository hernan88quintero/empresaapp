package com.hernan.empresaapp.repository;

import com.hernan.empresaapp.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Productos cuyo stock está por debajo o igual al mínimo configurado */
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosConStockBajo();

    boolean existsByCodigo(String codigo);
}