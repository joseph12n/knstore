package com.mycompany.knstore.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import com.mycompany.knstore.domain.enumeration.UbicacionBodega;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.repository.UserRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the payment flow endpoints (iniciar, callback, historial,
 * reembolso) and the invoice PDF download, including role access control.
 */
@IntegrationTest
@AutoConfigureMockMvc
class PagoFlujoResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoPrecioRepository productoPrecioRepository;

    @Autowired
    private ProductoInventarioRepository productoInventarioRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Cuenta cuenta;

    private Direccion direccion;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setNumDocumento("1234567890");
        cuenta.setPrimerNombre("Flujo");
        cuenta.setSegundoNombre("De");
        cuenta.setPrimerApellido("Test");
        cuenta.setSegundoApellido("Pagos");
        cuenta.setGenero(com.mycompany.knstore.domain.enumeration.Genero.FEMENINO);
        cuenta.setFechaNacimiento(java.time.LocalDate.of(1992, 5, 5));
        cuenta.setCelular("3001112233");
        cuenta.setTelefono("6011112233");
        com.mycompany.knstore.domain.TipoDocumento tipoDocumento = new com.mycompany.knstore.domain.TipoDocumento();
        tipoDocumento.setId("tipo-doc-flujo");
        tipoDocumento.setNombreTipo("CC");
        tipoDocumento.setSigla("CC");
        tipoDocumento.setEstado(com.mycompany.knstore.domain.enumeration.EstadoTipoDocumento.ACTIVO);
        mongoTemplate.save(tipoDocumento);
        cuenta.setTipoDocumento(tipoDocumento);
        cuenta.setActivo(true);
        cuenta = cuentaRepository.save(cuenta);

        direccion = new Direccion();
        direccion.setDireccion("Calle 10 #20-30");
        direccion.setMunicipio("Bogota");
        direccion.setDepartamento("Cundinamarca");
        direccion.setActivo(true);
        direccion.setTelefonoContacto("3001112233");
        direccion.setDestinatario("Flujo Test");
        direccion.setCodigoPostal("110111");
        direccion.setCuenta(cuenta);
        direccion = direccionRepository.save(direccion);

        pedido = new Pedido();
        pedido.setNumeroPedido("PED-TEST-000001");
        pedido.setEstado(EstadoPedido.PENDING);
        pedido.setSubtotal(new BigDecimal("100000.00"));
        pedido.setIvaTotal(new BigDecimal("19000.00"));
        pedido.setCostoEnvio(new BigDecimal("9900.00"));
        pedido.setDescuento(BigDecimal.ZERO);
        pedido.setTotal(new BigDecimal("128900.00"));
        pedido.setDireccion(direccion);
        pedido.setCuenta(cuenta);
        pedido = pedidoRepository.save(pedido);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        facturaRepository.deleteAll();
        pagoRepository.deleteAll();
        pedidoRepository.deleteAll();
        direccionRepository.deleteAll();
        cuentaRepository.deleteAll();
        userRepository.deleteAll();
        productoRepository.deleteAll();
        productoPrecioRepository.deleteAll();
        productoInventarioRepository.deleteAll();
    }

    private void autenticarComo(String userId, String rol) {
        Instant now = Instant.now();
        Jwt jwt = Jwt.withTokenValue("token")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(60))
            .claim(SecurityUtils.USER_ID_CLAIM, userId)
            .header("alg", "none")
            .build();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(jwt, "token", List.of(new SimpleGrantedAuthority(rol))));
        SecurityContextHolder.setContext(context);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void flujoCompletoIniciarCallbackHistorialYReembolso() throws Exception {
        String iniciarResponse = mockMvc
            .perform(
                post("/api/pagos/iniciar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("pedidoId", pedido.getId())))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("APPROVED"))
            .andExpect(jsonPath("$.referenciaPasarela").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String pagoId = om.readTree(iniciarResponse).get("id").asText();
        String referencia = om.readTree(iniciarResponse).get("referenciaPasarela").asText();

        // La pasarela es simbolica: un callback posterior no vuelve a procesar el pago (idempotente).
        mockMvc
            .perform(
                post("/api/pagos/callback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("referencia", referencia, "estado", "APPROVED", "monto", 128900.00)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("APPROVED"));

        mockMvc
            .perform(get("/api/pagos/{id}/historial", pagoId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].entidad").value("PAGO"))
            .andExpect(jsonPath("$[0].campo").value("estado"));

        mockMvc
            .perform(
                post("/api/pagos/{id}/reembolso", pagoId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("motivo", "devolucion")))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("REFUNDED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void iniciarPagoDobleEsIdempotenteYCallbackRechazadoNoReverte() throws Exception {
        String primeraRespuesta = mockMvc
            .perform(
                post("/api/pagos/iniciar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("pedidoId", pedido.getId())))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("APPROVED"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String referencia = om.readTree(primeraRespuesta).get("referenciaPasarela").asText();

        // Segundo iniciar: no reprocesa, devuelve el mismo pago aprobado.
        mockMvc
            .perform(
                post("/api/pagos/iniciar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("pedidoId", pedido.getId())))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("APPROVED"))
            .andExpect(jsonPath("$.referenciaPasarela").value(referencia));

        List<Pago> pagos = pagoRepository.findByPedidoId(pedido.getId(), org.springframework.data.domain.Pageable.unpaged()).getContent();
        assertThat(pagos).hasSize(1);

        // Un callback externo con estado REJECTED no puede bajar un pago ya aprobado.
        mockMvc
            .perform(
                post("/api/pagos/callback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("referencia", referencia, "estado", "REJECTED", "monto", 128900.00)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("APPROVED"));

        Pago persistido = pagoRepository.findByReferenciaPasarela(referencia).orElseThrow();
        assertThat(persistido.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(persistido.getCodigoAutorizacion()).startsWith("AUT-");
    }

    @Test
    void checkoutCreaElPagoAprobadoYElIniciarPosteriorNoReprocesa() throws Exception {
        ProductoPrecio precio = new ProductoPrecio();
        precio.setPrecioCompra(new BigDecimal("50000.00"));
        precio.setPrecioVenta(new BigDecimal("100000.00"));
        precio.setPrecioAdicional(BigDecimal.ZERO);
        precio.setGanancia(new BigDecimal("50000.00"));
        precio = productoPrecioRepository.save(precio);

        ProductoInventario inventario = new ProductoInventario();
        inventario.setStock(5);
        inventario.setStockMinimo(1);
        inventario.setUbicacionBodega(UbicacionBodega.BODEGA_PRINCIPAL);
        inventario.setGarantiaMeses(6);
        inventario = productoInventarioRepository.save(inventario);

        Producto producto = new Producto();
        producto.setNombre("Tenis Checkout");
        producto.setSlug("tenis-checkout");
        producto.setSku("TC-1");
        producto.setColor("Negro");
        producto.setTalla("40");
        producto.setUnidadMedida("Par");
        producto.setDestacado(false);
        producto.setActivo(true);
        producto.setPrecio(precio);
        producto.setInventario(inventario);
        producto = productoRepository.save(producto);

        User user = new User();
        user.setId("user-flujo-checkout");
        user.setLogin("flujo-checkout");
        user.setPassword("x".repeat(60));
        user.setActivated(true);
        user.setEmail("flujo-checkout@test.com");
        userRepository.save(user);
        cuenta.setUser(user);
        cuentaRepository.save(cuenta);

        // Principal JWT con el claim userId para que el checkout resuelva la cuenta autenticada.
        autenticarComo(user.getId(), AuthoritiesConstants.CLIENTE);

        Map<String, Object> checkoutRequest = Map.of(
            "direccionId",
            direccion.getId(),
            "metodoPago",
            MetodoPago.NEQUI.name(),
            "tipoServicioEnvio",
            TipoServicioEnvio.ESTANDAR.name(),
            "notasCliente",
            "pago aprobado desde el checkout",
            "items",
            List.of(Map.of("productoId", producto.getId(), "cantidad", 1))
        );

        String checkoutResponse = mockMvc
            .perform(post("/api/pedidos/checkout").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkoutRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        String pedidoId = om.readTree(checkoutResponse).get("pedido").get("id").asText();

        // El pago nace APPROVED directamente desde el checkout, en la misma transaccion.
        List<Pago> pagos = pagoRepository.findByPedidoId(pedidoId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        assertThat(pagos).hasSize(1);
        Pago pago = pagos.get(0);
        assertThat(pago.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(pago.getReferenciaPasarela()).startsWith("SIM-");
        assertThat(pago.getCodigoAutorizacion()).startsWith("AUT-");
        assertThat(pago.getFechaPago()).isNotNull();
        assertThat(pago.getMonto()).isEqualByComparingTo(new BigDecimal("109900.00"));

        // /iniciar posterior es idempotente: mismo pago, sin reprocesar ni duplicar.
        mockMvc
            .perform(
                post("/api/pagos/iniciar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("pedidoId", pedidoId)))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("APPROVED"))
            .andExpect(jsonPath("$.referenciaPasarela").value(pago.getReferenciaPasarela()));

        assertThat(pagoRepository.findByPedidoId(pedidoId, org.springframework.data.domain.Pageable.unpaged()).getContent()).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeInvocarElCallbackDeLaPasarela() throws Exception {
        Pago pago = new Pago();
        pago.setEstado(EstadoPago.PENDING);
        pago.setMonto(new BigDecimal("128900.00"));
        pago.setMetodoPago(MetodoPago.NEQUI);
        pago.setReferenciaPasarela("SIM-CLIENTE-PROHIBIDO");
        pago.setPedido(pedido);
        pagoRepository.save(pago);

        mockMvc
            .perform(
                post("/api/pagos/callback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("referencia", "SIM-CLIENTE-PROHIBIDO", "estado", "APPROVED", "monto", 128900.00)))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void pagoAprobadoGeneraFacturaConPdfDescargable() throws Exception {
        String iniciarResponse = mockMvc
            .perform(
                post("/api/pagos/iniciar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("pedidoId", pedido.getId())))
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String referencia = om.readTree(iniciarResponse).get("referenciaPasarela").asText();

        mockMvc
            .perform(
                post("/api/pagos/callback")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("referencia", referencia, "estado", "APPROVED", "monto", 128900.00)))
            )
            .andExpect(status().isOk());

        Factura factura = facturaRepository.findAll().stream().findFirst().orElseThrow();
        assertThat(factura.getNumero()).startsWith("FE-");

        mockMvc
            .perform(get("/api/facturas/{id}/pdf", factura.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeReembolsar() throws Exception {
        Pago pago = new Pago();
        pago.setEstado(EstadoPago.APPROVED);
        pago.setMonto(new BigDecimal("128900.00"));
        pago.setMetodoPago(MetodoPago.NEQUI);
        pago.setPedido(pedido);
        pago = pagoRepository.save(pago);

        mockMvc
            .perform(
                post("/api/pagos/{id}/reembolso", pago.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("motivo", "test")))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dobleReembolsoEsRechazadoCon400() throws Exception {
        Pago pago = new Pago();
        pago.setEstado(EstadoPago.REFUNDED);
        pago.setMonto(new BigDecimal("128900.00"));
        pago.setMetodoPago(MetodoPago.NEQUI);
        pago.setPedido(pedido);
        pago = pagoRepository.save(pago);

        mockMvc
            .perform(
                post("/api/pagos/{id}/reembolso", pago.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(Map.of("motivo", "otra vez")))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void clienteVeSoloSusPagosEnElListado() throws Exception {
        User userPropio = new User();
        userPropio.setId("user-listado-pago");
        userPropio.setLogin("cliente-listado-pago");
        userPropio.setPassword("x".repeat(60));
        userPropio.setActivated(true);
        userPropio.setEmail("cliente-listado-pago@test.com");
        userRepository.save(userPropio);
        cuenta.setUser(userPropio);
        cuentaRepository.save(cuenta);

        Pago pagoPropio = new Pago();
        pagoPropio.setEstado(EstadoPago.APPROVED);
        pagoPropio.setMonto(new BigDecimal("128900.00"));
        pagoPropio.setMetodoPago(MetodoPago.NEQUI);
        pagoPropio.setReferenciaPasarela("SIM-LISTADO-PROPIO");
        pagoPropio.setPedido(pedido);
        pagoPropio = pagoRepository.save(pagoPropio);

        // Otra cuenta con su propio pedido y pago: no debe aparecer en el listado.
        Cuenta otraCuenta = new Cuenta();
        otraCuenta.setNumDocumento("9876543210");
        otraCuenta.setPrimerNombre("Otro");
        otraCuenta.setSegundoNombre("Usuario");
        otraCuenta.setPrimerApellido("De");
        otraCuenta.setSegundoApellido("Prueba");
        otraCuenta.setGenero(com.mycompany.knstore.domain.enumeration.Genero.MASCULINO);
        otraCuenta.setFechaNacimiento(java.time.LocalDate.of(1990, 1, 1));
        otraCuenta.setCelular("3004445566");
        otraCuenta.setTelefono("6014445566");
        otraCuenta.setTipoDocumento(cuenta.getTipoDocumento());
        otraCuenta.setActivo(true);
        otraCuenta = cuentaRepository.save(otraCuenta);

        Pedido pedidoAjeno = new Pedido();
        pedidoAjeno.setNumeroPedido("PED-TEST-000002");
        pedidoAjeno.setEstado(EstadoPedido.PENDING);
        pedidoAjeno.setSubtotal(new BigDecimal("50000.00"));
        pedidoAjeno.setIvaTotal(new BigDecimal("9500.00"));
        pedidoAjeno.setCostoEnvio(BigDecimal.ZERO);
        pedidoAjeno.setDescuento(BigDecimal.ZERO);
        pedidoAjeno.setTotal(new BigDecimal("59500.00"));
        pedidoAjeno.setDireccion(direccion);
        pedidoAjeno.setCuenta(otraCuenta);
        pedidoAjeno = pedidoRepository.save(pedidoAjeno);

        Pago pagoAjeno = new Pago();
        pagoAjeno.setEstado(EstadoPago.PENDING);
        pagoAjeno.setMonto(new BigDecimal("59500.00"));
        pagoAjeno.setMetodoPago(MetodoPago.NEQUI);
        pagoAjeno.setReferenciaPasarela("SIM-LISTADO-AJENO");
        pagoAjeno.setPedido(pedidoAjeno);
        pagoAjeno = pagoRepository.save(pagoAjeno);

        autenticarComo(userPropio.getId(), AuthoritiesConstants.CLIENTE);

        mockMvc
            .perform(get("/api/pagos").param("page", "0").param("size", "10").param("sort", "id,asc"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(jsonPath("$[0].id").value(pagoPropio.getId()))
            .andExpect(jsonPath("$[0].estado").value("APPROVED"));
    }
}
