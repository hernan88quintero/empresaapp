package com.hernan.empresaapp.repository;

import com.hernan.empresaapp.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}
