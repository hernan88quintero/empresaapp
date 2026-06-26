package com.hernan.empresaapp.controller;

import com.hernan.empresaapp.dto.request.LoginRequest;
import com.hernan.empresaapp.dto.request.RegisterRequest;
import com.hernan.empresaapp.dto.response.AuthResponse;
import com.hernan.empresaapp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticación.
 * No requieren token JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/login — Devuelve token JWT si las credenciales son correctas */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** POST /api/auth/registro — Crea un usuario y devuelve token */
    @PostMapping("/registro")
    public AuthResponse registro(@Valid @RequestBody RegisterRequest request) {
        return authService.registrar(request);
    }
}
