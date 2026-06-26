package com.hernan.empresaapp.dto.response;

import com.hernan.empresaapp.model.Usuario;
import com.hernan.empresaapp.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos del usuario expuestos en la API (sin contraseña).
 */
@Data
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaCreacion()
        );
    }
}
