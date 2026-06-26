package com.hernan.empresaapp.repository;

import com.hernan.empresaapp.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}
