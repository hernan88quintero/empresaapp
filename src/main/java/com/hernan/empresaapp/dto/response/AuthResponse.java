package com.hernan.empresaapp.dto.response;

import com.hernan.empresaapp.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Respuesta tras login o registro exitoso: token JWT y datos básicos del usuario.
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Rol rol;

    public AuthResponse(String token, Long id, String username, String email, Rol rol) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.rol = rol;
    }
}
