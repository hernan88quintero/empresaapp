package com.hernan.empresaapp.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {

    private String mensaje;
    private int status;
    private LocalDateTime fecha;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String mensaje, int status, LocalDateTime fecha) {
        this.mensaje = mensaje;
        this.status = status;
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}