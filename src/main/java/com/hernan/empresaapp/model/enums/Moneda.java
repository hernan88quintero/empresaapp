package com.hernan.empresaapp.model.enums;

public enum Moneda {
    USD("Dólar estadounidense"),
    EUR("Euro"),
    ARS("Peso argentino"),
    BRL("Real brasileño"),
    CLP("Peso chileno"),
    UYU("Peso uruguayo"),
    GBP("Libra esterlina"),
    JPY("Yen japonés");

    private final String nombre;

    Moneda(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
