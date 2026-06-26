package com.hernan.empresaapp.service;

import com.hernan.empresaapp.exception.ResourceNotFoundException;
import com.hernan.empresaapp.model.Proveedor;
import com.hernan.empresaapp.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }

    public Proveedor crear(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDocumento(datos.getDocumento());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        existente.setDireccion(datos.getDireccion());
        existente.setActivo(datos.isActivo());
        return proveedorRepository.save(existente);
    }

    public void eliminar(Long id) {
        proveedorRepository.delete(buscarPorId(id));
    }
}
