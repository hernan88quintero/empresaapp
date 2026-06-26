package com.hernan.empresaapp.config;

import com.hernan.empresaapp.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Ejecuta la carga de datos demo al iniciar la aplicación.
 */
@Component
@ConditionalOnProperty(name = "app.demo.seed", havingValue = "true", matchIfMissing = true)
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final String MARCA_DEMO = "SEED-DEMO-001";

    private final ProductoRepository productoRepository;
    private final DemoDataSeedService demoDataSeedService;

    public DemoDataSeeder(ProductoRepository productoRepository, DemoDataSeedService demoDataSeedService) {
        this.productoRepository = productoRepository;
        this.demoDataSeedService = demoDataSeedService;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.existsByCodigo(MARCA_DEMO)) {
            log.info("Datos demo ya cargados. Omitiendo seed.");
            return;
        }
        demoDataSeedService.cargarDatosDemo();
    }
}
