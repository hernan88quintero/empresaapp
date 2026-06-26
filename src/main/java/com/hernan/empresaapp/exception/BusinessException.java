package com.hernan.empresaapp.exception;

/**
 * Error de reglas de negocio (stock insuficiente, usuario duplicado, etc.).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
