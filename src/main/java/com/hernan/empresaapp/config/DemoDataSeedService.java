package com.hernan.empresaapp.config;

import com.hernan.empresaapp.dto.request.CompraRequest;
import com.hernan.empresaapp.dto.request.VentaRequest;
import com.hernan.empresaapp.model.*;
import com.hernan.empresaapp.model.enums.Rol;
import com.hernan.empresaapp.repository.*;
import com.hernan.empresaapp.service.CompraService;
import com.hernan.empresaapp.service.InventarioService;
import com.hernan.empresaapp.service.VentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DemoDataSeedService {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeedService.class);

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ClienteRepository clienteRepository;
    private final CompraService compraService;
    private final VentaService ventaService;
    private final InventarioService inventarioService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    public DemoDataSeedService(
            UsuarioRepository usuarioRepository,
            EmpleadoRepository empleadoRepository,
            ProductoRepository productoRepository,
            ProveedorRepository proveedorRepository,
            ClienteRepository clienteRepository,
            CompraService compraService,
            VentaService ventaService,
            InventarioService inventarioService,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.clienteRepository = clienteRepository;
        this.compraService = compraService;
        this.ventaService = ventaService;
        this.inventarioService = inventarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void cargarDatosDemo() {
        log.info("=== Cargando datos de demostración ===");

        crearUsuarioSiNoExiste(adminUsername, adminEmail, adminPassword, Rol.ADMIN, true);
        Usuario vendedor = crearUsuarioSiNoExiste("vendedor", "vendedor@empresa.com", "vendedor123", Rol.EMPLEADO, true);
        Usuario bodeguero = crearUsuarioSiNoExiste("bodeguero", "bodega@empresa.com", "bodega123", Rol.EMPLEADO, true);
        crearUsuarioSiNoExiste("inactivo", "inactivo@empresa.com", "inactivo123", Rol.EMPLEADO, false);

        Producto laptop = crearProducto("SEED-DEMO-001", "Laptop HP 15", "Laptop 15 pulgadas 16GB RAM", 25, 10, "850.00");
        Producto mouse = crearProducto("SEED-DEMO-002", "Mouse Logitech", "Mouse inalámbrico", 3, 10, "45.00");
        Producto teclado = crearProducto("SEED-DEMO-003", "Teclado Mecánico", "Teclado RGB switch blue", 0, 5, "120.00");
        Producto monitor = crearProducto("SEED-DEMO-004", "Monitor 24 pulgadas", "Monitor Full HD IPS", 18, 5, "320.00");
        Producto webcam = crearProducto("SEED-DEMO-005", "Webcam HD", "Cámara 1080p con micrófono", 2, 8, "95.00");
        Producto hub = crearProducto("SEED-DEMO-006", "Hub USB-C", "Hub 7 en 1", 50, 20, "65.00");

        Proveedor provTech = crearProveedor("Distribuidora TechSur", "20111111111", "ventas@techsur.com", true);
        Proveedor provGlobal = crearProveedor("Importadora GlobalPC", "20222222222", "compras@globalpc.com", true);
        crearProveedor("Proveedor Inactivo SA", "20333333333", "viejo@inactivo.com", false);

        Cliente cliAndina = crearCliente("Empresa Andina SAC", "20444444444", "compras@andina.com", true);
        Cliente cliLima = crearCliente("Comercial Lima Norte", "20555555555", "ventas@limanorte.com", true);
        crearCliente("Cliente Moroso EIRL", "20666666666", "moroso@cliente.com", false);

        crearEmpleado("María", "García", "12345678", "maria@empresa.com", "Vendedora", "Ventas", vendedor, true);
        crearEmpleado("Carlos", "Mendoza", "87654321", "carlos@empresa.com", "Bodeguero", "Logística", bodeguero, true);
        crearEmpleado("Ana", "Torres", "11223344", "ana@empresa.com", "Analista RRHH", "Recursos Humanos", null, true);
        crearEmpleado("Pedro", "Inactivo", "99887766", "pedro@empresa.com", "Ex empleado", "Ventas", null, false);

        compraService.registrar(crearCompraRequest(provTech.getId(), "C-2026-001",
                "Compra inicial de equipos",
                List.of(
                        linea(laptop.getId(), 20, "720.00"),
                        linea(mouse.getId(), 50, "35.00"),
                        linea(monitor.getId(), 10, "280.00")
                )));

        compraService.registrar(crearCompraRequest(provGlobal.getId(), "C-2026-002",
                "Reposición teclados sin stock",
                List.of(linea(teclado.getId(), 30, "95.00"))));

        compraService.registrar(crearCompraRequest(provTech.getId(), "C-2026-003",
                "Compra accesorios",
                List.of(
                        linea(webcam.getId(), 15, "75.00"),
                        linea(hub.getId(), 25, "55.00")
                )));

        Producto monitorActualizado = productoRepository.findById(monitor.getId()).orElseThrow();
        inventarioService.registrarAjuste(monitorActualizado, -2, "Ajuste por inventario físico - 2 monitores dañados");

        ventaService.registrar(crearVentaRequest(cliAndina.getId(), "V-2026-001",
                "Venta corporativa laptops",
                List.of(
                        lineaVenta(laptop.getId(), 5, "900.00"),
                        lineaVenta(mouse.getId(), 10, "55.00")
                )));

        ventaService.registrar(crearVentaRequest(cliLima.getId(), "V-2026-002",
                "Venta monitores y teclados",
                List.of(
                        lineaVenta(monitor.getId(), 3, "380.00"),
                        lineaVenta(teclado.getId(), 8, "150.00")
                )));

        ventaService.registrar(crearVentaRequest(cliAndina.getId(), "V-2026-003",
                "Venta accesorios",
                List.of(lineaVenta(hub.getId(), 12, "80.00"))));

        log.info("=== Datos demo cargados correctamente ===");
        log.info("Usuarios: admin/{} | vendedor/vendedor123 | bodeguero/bodega123", adminPassword);
        log.info("Productos: 6 | Proveedores: 3 | Clientes: 3 | Empleados: 4");
        log.info("Compras: 3 | Ventas: 3 | Movimientos: ENTRADA, SALIDA y AJUSTE");
    }

    private Usuario crearUsuarioSiNoExiste(String username, String email, String password, Rol rol, boolean activo) {
        return usuarioRepository.findByUsername(username).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            u.setRol(rol);
            u.setActivo(activo);
            return usuarioRepository.save(u);
        });
    }

    private Producto crearProducto(String codigo, String nombre, String descripcion,
                                   int stock, int stockMinimo, String precio) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setStock(stock);
        p.setStockMinimo(stockMinimo);
        p.setPrecio(new BigDecimal(precio));
        return productoRepository.save(p);
    }

    private Proveedor crearProveedor(String nombre, String documento, String email, boolean activo) {
        Proveedor p = new Proveedor();
        p.setNombre(nombre);
        p.setDocumento(documento);
        p.setEmail(email);
        p.setTelefono("01-555-0000");
        p.setDireccion("Av. Industrial 100, Lima");
        p.setActivo(activo);
        return proveedorRepository.save(p);
    }

    private Cliente crearCliente(String nombre, String documento, String email, boolean activo) {
        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setDocumento(documento);
        c.setEmail(email);
        c.setTelefono("999-000-111");
        c.setDireccion("Jr. Comercio 250");
        c.setActivo(activo);
        return clienteRepository.save(c);
    }

    private void crearEmpleado(String nombre, String apellido, String documento, String email,
                               String cargo, String departamento, Usuario usuario, boolean activo) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido(apellido);
        e.setDocumento(documento);
        e.setEmail(email);
        e.setTelefono("987-654-321");
        e.setCargo(cargo);
        e.setDepartamento(departamento);
        e.setSalario(new BigDecimal("2800.00"));
        e.setFechaIngreso(LocalDate.of(2024, 3, 15));
        e.setUsuario(usuario);
        e.setActivo(activo);
        empleadoRepository.save(e);
    }

    private CompraRequest crearCompraRequest(Long proveedorId, String factura, String obs,
                                             List<CompraRequest.DetalleCompraRequest> detalles) {
        CompraRequest req = new CompraRequest();
        req.setProveedorId(proveedorId);
        req.setNumeroFactura(factura);
        req.setObservacion(obs);
        req.setDetalles(detalles);
        return req;
    }

    private CompraRequest.DetalleCompraRequest linea(Long productoId, int cantidad, String precio) {
        CompraRequest.DetalleCompraRequest d = new CompraRequest.DetalleCompraRequest();
        d.setProductoId(productoId);
        d.setCantidad(cantidad);
        d.setPrecioUnitario(new BigDecimal(precio));
        return d;
    }

    private VentaRequest crearVentaRequest(Long clienteId, String factura, String obs,
                                           List<VentaRequest.DetalleVentaRequest> detalles) {
        VentaRequest req = new VentaRequest();
        req.setClienteId(clienteId);
        req.setNumeroFactura(factura);
        req.setObservacion(obs);
        req.setDetalles(detalles);
        return req;
    }

    private VentaRequest.DetalleVentaRequest lineaVenta(Long productoId, int cantidad, String precio) {
        VentaRequest.DetalleVentaRequest d = new VentaRequest.DetalleVentaRequest();
        d.setProductoId(productoId);
        d.setCantidad(cantidad);
        d.setPrecioUnitario(new BigDecimal(precio));
        return d;
    }
}
