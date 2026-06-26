package com.hernan.empresaapp.dto.request;

import com.hernan.empresaapp.model.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos para actualizar un usuario existente.
 */
@Data
public class UpdateUsuarioRequest {

    @Email(message = "Email inválido")
    private String email;

    private Rol rol;

    private Boolean activo;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
