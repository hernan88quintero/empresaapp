package com.hernan.empresaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de la aplicación Spring Boot.
 * Escanea componentes en este paquete y subpaquetes (controller, service, etc.).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
@EnableScheduling
public class EmpresaappApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpresaappApplication.class, args);
    }
}
