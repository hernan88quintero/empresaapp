package com.hernan.empresaapp.dto.request;

import com.hernan.empresaapp.model.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos para registrar un nuevo usuario en el sistema.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    /** Si no se envía, se asigna EMPLEADO por defecto */
    private Rol rol;
}
