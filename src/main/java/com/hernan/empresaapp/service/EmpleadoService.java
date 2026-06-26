package com.hernan.empresaapp.service;

import com.hernan.empresaapp.exception.ResourceNotFoundException;
import com.hernan.empresaapp.model.Empleado;
import com.hernan.empresaapp.model.Usuario;
import com.hernan.empresaapp.repository.EmpleadoRepository;
import com.hernan.empresaapp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CRUD de empleados y vinculación opcional con un usuario del sistema.
 */
@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, UsuarioRepository usuarioRepository) {
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }

    public Empleado buscarPorId(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", id));
    }

    public Empleado crear(Empleado empleado) {
        if (empleado.getUsuario() != null && empleado.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(empleado.getUsuario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", empleado.getUsuario().getId()));
            empleado.setUsuario(usuario);
        } else {
            empleado.setUsuario(null);
        }
        return empleadoRepository.save(empleado);
    }

    public Empleado actualizar(Long id, Empleado datos) {
        Empleado existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setDocumento(datos.getDocumento());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        existente.setCargo(datos.getCargo());
        existente.setDepartamento(datos.getDepartamento());
        existente.setSalario(datos.getSalario());
        existente.setFechaIngreso(datos.getFechaIngreso());
        existente.setActivo(datos.isActivo());

        if (datos.getUsuario() != null && datos.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(datos.getUsuario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", datos.getUsuario().getId()));
            existente.setUsuario(usuario);
        }

        return empleadoRepository.save(existente);
    }

    public void eliminar(Long id) {
        empleadoRepository.delete(buscarPorId(id));
    }
}
