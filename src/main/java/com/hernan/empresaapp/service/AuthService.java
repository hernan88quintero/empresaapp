package com.hernan.empresaapp.service;

import com.hernan.empresaapp.dto.request.LoginRequest;
import com.hernan.empresaapp.dto.request.RegisterRequest;
import com.hernan.empresaapp.dto.response.AuthResponse;
import com.hernan.empresaapp.exception.BusinessException;
import com.hernan.empresaapp.model.Usuario;
import com.hernan.empresaapp.model.enums.Rol;
import com.hernan.empresaapp.repository.UsuarioRepository;
import com.hernan.empresaapp.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Lógica de autenticación: registro, login y generación de JWT.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final com.hernan.empresaapp.security.CustomUserDetailsService userDetailsService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            com.hernan.empresaapp.security.CustomUserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /** Registra un usuario nuevo con contraseña hasheada */
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol() != null ? request.getRol() : Rol.EMPLEADO);

        Usuario guardado = usuarioRepository.save(usuario);
        UserDetails userDetails = userDetailsService.loadUserByUsername(guardado.getUsername());
        String token = jwtService.generarToken(userDetails);

        return new AuthResponse(token, guardado.getId(), guardado.getUsername(),
                guardado.getEmail(), guardado.getRol());
    }

    /** Valida credenciales y devuelve un token JWT */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
        String token = jwtService.generarToken(userDetails);

        return new AuthResponse(token, usuario.getId(), usuario.getUsername(),
                usuario.getEmail(), usuario.getRol());
    }
}
