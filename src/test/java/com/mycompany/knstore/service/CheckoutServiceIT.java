package com.mycompany.knstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.domain.enumeration.EstadoIVA;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import com.mycompany.knstore.domain.enumeration.UbicacionBodega;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.service.dto.CheckoutItemDTO;
import com.mycompany.knstore.service.dto.CheckoutRequestDTO;
import com.mycompany.knstore.service.dto.CheckoutResultDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Tests de integracion del checkout atomico sobre el replica set (RNF-023) y
 * de la concurrencia del stock (RNF-024).
 */
@IntegrationTest
class CheckoutServiceIT {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoPrecioRepository productoPrecioRepository;

    @Autowired
    private ProductoInventarioRepository productoInventarioRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private CategoriaIVA categoriaIva;

    private Marca marca;

    private Categoria categoria;

    private Subcategoria subcategoria;

    private ProductoPrecio precio;

    private ProductoInventario inventario;

    private Producto producto;

    private Cuenta cuenta;

    private Direccion direccion;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        categoriaIva = new CategoriaIVA();
        categoriaIva.setNombre("IVA 19%");
        categoriaIva.setPorcentaje(new BigDecimal("19"));
        categoriaIva.setEstado(EstadoIVA.ACTIVO);
        categoriaIva = mongoTemplate.save(categoriaIva);

        marca = new Marca();
        marca.setNombre("MarcaTest");
        marca.setSlug("marca-test");
        marca = mongoTemplate.save(marca);

        categoria = new Categoria();
        categoria.setNombre("CategoriaTest");
        categoria.setSlug("categoria-test");
        categoria.setDescripcion("test");
        categoria.setActivo(true);
        categoria = mongoTemplate.save(categoria);

        subcategoria = new Subcategoria();
        subcategoria.setNombre("Sub Test");
        subcategoria.setSlug("sub-test");
        subcategoria.setDescripcion("test");
        subcategoria.setActivo(true);
        subcategoria.setCategoria(categoria);
        subcategoria = mongoTemplate.save(subcategoria);

        precio = new ProductoPrecio();
        precio.setPrecioCompra(new BigDecimal("50000.00"));
        precio.setPrecioVenta(new BigDecimal("100000.00"));
        precio.setPrecioAdicional(BigDecimal.ZERO);
        precio.setGanancia(new BigDecimal("50000.00"));
        precio = productoPrecioRepository.save(precio);

        inventario = new ProductoInventario();
        inventario.setStock(10);
        inventario.setStockMinimo(1);
        inventario.setUbicacionBodega(UbicacionBodega.BODEGA_PRINCIPAL);
        inventario.setGarantiaMeses(6);
        inventario = productoInventarioRepository.save(inventario);

        producto = new Producto();
        producto.setNombre("Tenis Test");
        producto.setSlug("tenis-test");
        producto.setSku("TT-1");
        producto.setColor("Negro");
        producto.setTalla("40");
        producto.setUnidadMedida("Par");
        producto.setDestacado(false);
        producto.setActivo(true);
        producto.setPrecio(precio);
        producto.setInventario(inventario);
        producto.setCategoria(categoria);
        producto.setSubcategoria(subcategoria);
        producto.setMarca(marca);
        producto.setCategoriaIva(categoriaIva);
        producto = productoRepository.save(producto);

        cuenta = crearCuenta("cuenta-checkout");
        direccion = crearDireccion(cuenta);
        carrito = crearCarrito(cuenta);
    }

    @AfterEach
    void tearDown() {
        itemCarritoRepository.deleteAll();
        carritoRepository.deleteAll();
        direccionRepository.deleteAll();
        cuentaRepository.deleteAll();
        envioRepository.deleteAll();
        pagoRepository.deleteAll();
        facturaRepository.deleteAll();
        itemPedidoRepository.deleteAll();
        pedidoRepository.deleteAll();
        productoRepository.deleteAll();
        productoPrecioRepository.deleteAll();
        productoInventarioRepository.deleteAll();
        mongoTemplate.dropCollection("subcategoria");
        mongoTemplate.dropCollection("categoria");
        mongoTemplate.dropCollection("marca");
        mongoTemplate.dropCollection("categoriaiva");
    }

    private Cuenta crearCuenta(String login) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumDocumento("1" + Integer.toUnsignedString(login.hashCode()));
        cuenta.setPrimerNombre("Cliente");
        cuenta.setSegundoNombre("Segundo");
        cuenta.setPrimerApellido("Test");
        cuenta.setSegundoApellido("Apellido");
        cuenta.setGenero(com.mycompany.knstore.domain.enumeration.Genero.MASCULINO);
        cuenta.setFechaNacimiento(java.time.LocalDate.of(1990, 1, 1));
        cuenta.setCelular("3000000000");
        cuenta.setTelefono("6010000000");
        com.mycompany.knstore.domain.TipoDocumento tipoDocumento = new com.mycompany.knstore.domain.TipoDocumento();
        tipoDocumento.setId("tipo-doc-" + login);
        tipoDocumento.setNombreTipo("CC");
        tipoDocumento.setSigla("CC");
        tipoDocumento.setEstado(com.mycompany.knstore.domain.enumeration.EstadoTipoDocumento.ACTIVO);
        mongoTemplate.save(tipoDocumento);
        cuenta.setTipoDocumento(tipoDocumento);
        cuenta.setActivo(true);
        return cuentaRepository.save(cuenta);
    }

    private Direccion crearDireccion(Cuenta cuenta) {
        Direccion direccion = new Direccion();
        direccion.setDireccion("Calle 1 #2-3");
        direccion.setMunicipio("Bogota");
        direccion.setDepartamento("Cundinamarca");
        direccion.setActivo(true);
        direccion.setTelefonoContacto("3001234567");
        direccion.setDestinatario("Test");
        direccion.setCodigoPostal("110111");
        direccion.setCuenta(cuenta);
        return direccionRepository.save(direccion);
    }

    private Carrito crearCarrito(Cuenta cuenta) {
        Carrito carrito = new Carrito();
        carrito.setSubtotal(new BigDecimal("100000.00"));
        carrito.setFechaActualizacion(Instant.now());
        carrito.setCuenta(cuenta);
        carrito = carritoRepository.save(carrito);

        ItemCarrito item = new ItemCarrito();
        item.setCantidad(1);
        item.setPrecioUnitario(new BigDecimal("100000.00"));
        item.setSubtotal(new BigDecimal("100000.00"));
        item.setCarrito(carrito);
        item.setProducto(producto);
        itemCarritoRepository.save(item);
        return carrito;
    }

    private CheckoutRequestDTO requestDeCompra(int cantidad) {
        CheckoutItemDTO item = new CheckoutItemDTO();
        item.setProductoId(producto.getId());
        item.setCantidad(cantidad);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setDireccionId(direccion.getId());
        request.setMetodoPago(MetodoPago.NEQUI);
        request.setTipoServicioEnvio(TipoServicioEnvio.ESTANDAR);
        request.setItems(List.of(item));
        return request;
    }

    @Test
    void flujoFelizCreaPedidoItemsPagoAprobadoYEnvioPendientesYVaciaElCarrito() {
        CheckoutResultDTO result = checkoutService.checkout(cuenta, requestDeCompra(1));

        Pedido pedido = pedidoRepository.findById(result.getPedido().getId()).orElseThrow();
        // El pago se aprueba de inmediato, por lo que el pedido nace confirmado.
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMED);
        assertThat(pedido.getSubtotal()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(pedido.getIvaTotal()).isEqualByComparingTo(new BigDecimal("19000.00"));
        assertThat(pedido.getCostoEnvio()).isEqualByComparingTo(new BigDecimal("9900.00"));
        assertThat(pedido.getTotal()).isEqualByComparingTo(new BigDecimal("128900.00"));

        List<ItemPedido> items = itemPedidoRepository.findByPedidoId(pedido.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getSubtotal()).isEqualByComparingTo(new BigDecimal("100000.00"));

        Pago pago = pagoRepository.findByPedidoId(pedido.getId(), org.springframework.data.domain.Pageable.unpaged()).getContent().get(0);
        assertThat(pago.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(pago.getReferenciaPasarela()).startsWith("SIM-");
        assertThat(pago.getCodigoAutorizacion()).startsWith("AUT-");
        assertThat(pago.getFechaPago()).isNotNull();
        assertThat(pago.getIntentos()).isEqualTo(1);
        assertThat(pago.getMonto()).isEqualByComparingTo(new BigDecimal("128900.00"));

        Envio envio = envioRepository
            .findByPedidoId(pedido.getId(), org.springframework.data.domain.Pageable.unpaged())
            .getContent()
            .get(0);
        assertThat(envio.getEstado()).isEqualTo(com.mycompany.knstore.domain.enumeration.EstadoEnvio.PENDING);

        Factura factura = facturaRepository
            .findByPagoId(pago.getId(), org.springframework.data.domain.Pageable.unpaged())
            .getContent()
            .get(0);
        assertThat(factura.getNumero()).startsWith("FE-");
        assertThat(factura.getTotal()).isEqualByComparingTo(new BigDecimal("128900.00"));

        assertThat(productoInventarioRepository.findById(inventario.getId()).orElseThrow().getStock()).isEqualTo(9);
        assertThat(itemCarritoRepository.findByCarritoId(carrito.getId())).isEmpty();
    }

    @Test
    void stockInsuficienteRevierteTodaLaOperacion() {
        long pedidosAntes = pedidoRepository.count();

        assertThatThrownBy(() -> checkoutService.checkout(cuenta, requestDeCompra(99))).isInstanceOf(CheckoutException.class);

        assertThat(pedidoRepository.count()).isEqualTo(pedidosAntes);
        assertThat(pagoRepository.count()).isZero();
        assertThat(envioRepository.count()).isZero();
        assertThat(productoInventarioRepository.findById(inventario.getId()).orElseThrow().getStock()).isEqualTo(10);
    }

    @Test
    void elPrecioDelClienteEsIgnoradoElServidorUsaElDeLaBaseDeDatos() {
        // El checkout ya no acepta precioUnitario del cliente: el precio de venta
        // se resuelve siempre desde el producto en base de datos.
        CheckoutResultDTO result = checkoutService.checkout(cuenta, requestDeCompra(1));

        Pedido pedido = pedidoRepository.findById(result.getPedido().getId()).orElseThrow();
        List<ItemPedido> items = itemPedidoRepository.findByPedidoId(pedido.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getPrecioUnitario()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(pedido.getTotal()).isEqualByComparingTo(new BigDecimal("128900.00"));
    }

    @Test
    void carritoVacioEsRechazado() {
        CheckoutRequestDTO request = requestDeCompra(1);
        request.setItems(new ArrayList<>());

        assertThatThrownBy(() -> checkoutService.checkout(cuenta, request)).isInstanceOf(CheckoutException.class);
    }

    @Test
    void concurrenciaDe20ComprasNoGeneraSobreventa() throws Exception {
        int stockInicial = 10;
        int compradores = 20;
        ExecutorService executor = Executors.newFixedThreadPool(compradores);
        CountDownLatch inicio = new CountDownLatch(1);
        AtomicInteger exitosos = new AtomicInteger(0);
        AtomicInteger rechazados = new AtomicInteger(0);

        List<java.util.concurrent.Future<?>> futuros = new ArrayList<>();
        for (int i = 0; i < compradores; i++) {
            Cuenta cuentaComprador = crearCuenta("comprador-" + i);
            Direccion direccionComprador = crearDireccion(cuentaComprador);
            futuros.add(
                executor.submit(() -> {
                    try {
                        inicio.await();
                        CheckoutRequestDTO request = requestDeCompra(1);
                        request.setDireccionId(direccionComprador.getId());
                        checkoutService.checkout(cuentaComprador, request);
                        exitosos.incrementAndGet();
                    } catch (CheckoutException e) {
                        rechazados.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
            );
        }

        inicio.countDown();
        for (java.util.concurrent.Future<?> futuro : futuros) {
            futuro.get(60, TimeUnit.SECONDS);
        }
        executor.shutdown();

        assertThat(exitosos.get()).isEqualTo(stockInicial);
        assertThat(rechazados.get()).isEqualTo(compradores - stockInicial);
        Integer stockFinal = productoInventarioRepository.findById(inventario.getId()).orElseThrow().getStock();
        assertThat(stockFinal).isEqualTo(0);
        assertThat(stockFinal).isGreaterThanOrEqualTo(0);
    }
}
