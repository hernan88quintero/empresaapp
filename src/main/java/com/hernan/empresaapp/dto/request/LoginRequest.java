package com.hernan.empresaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Datos que envía el cliente para iniciar sesión.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "El usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
