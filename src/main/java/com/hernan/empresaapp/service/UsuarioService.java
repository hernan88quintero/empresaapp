package com.hernan.empresaapp.service;

import com.hernan.empresaapp.dto.request.RegisterRequest;
import com.hernan.empresaapp.dto.request.UpdateUsuarioRequest;
import com.hernan.empresaapp.dto.response.UsuarioResponseDTO;
import com.hernan.empresaapp.exception.BusinessException;
import com.hernan.empresaapp.exception.ResourceNotFoundException;
import com.hernan.empresaapp.model.Usuario;
import com.hernan.empresaapp.model.enums.Rol;
import com.hernan.empresaapp.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gestión de usuarios del sistema (listar, crear, actualizar).
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioResponseDTO.fromEntity(obtenerUsuario(id));
    }

    public UsuarioResponseDTO crear(RegisterRequest request) {
        validarDuplicados(request.getUsername(), request.getEmail(), null);

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol() != null ? request.getRol() : Rol.EMPLEADO);
        usuario.setActivo(true);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO actualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = obtenerUsuario(id);

        if (request.getEmail() != null) {
            if (usuarioRepository.existsByEmail(request.getEmail())
                    && !request.getEmail().equals(usuario.getEmail())) {
                throw new BusinessException("El email ya está registrado");
            }
            usuario.setEmail(request.getEmail());
        }
        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id) {
        usuarioRepository.delete(obtenerUsuario(id));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private void validarDuplicados(String username, String email, Long idExcluir) {
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            if (idExcluir == null || !u.getId().equals(idExcluir)) {
                throw new BusinessException("El nombre de usuario ya existe");
            }
        });
        usuarioRepository.findAll().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .filter(u -> idExcluir == null || !u.getId().equals(idExcluir))
                .findFirst()
                .ifPresent(u -> {
                    throw new BusinessException("El email ya está registrado");
                });
    }
}
