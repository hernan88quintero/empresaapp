package com.hernan.empresaapp.model;

import com.hernan.empresaapp.model.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Usuario del sistema para login.
 * La contraseña se guarda hasheada (BCrypt), nunca en texto plano.
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario para iniciar sesión */
    @Column(nullable = false, length = 50)
    private String username;

    /** Correo electrónico del usuario */
    @Column(nullable = false, length = 100)
    private String email;

    /** Contraseña hasheada con BCrypt */
    @Column(nullable = false)
    private String password;

    /** Rol que determina permisos en la aplicación */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.EMPLEADO;

    /** false = usuario deshabilitado, no puede iniciar sesión */
    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
