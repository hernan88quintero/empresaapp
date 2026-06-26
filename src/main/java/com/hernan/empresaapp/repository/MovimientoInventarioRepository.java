package com.hernan.empresaapp.repository;

import com.hernan.empresaapp.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);
}
