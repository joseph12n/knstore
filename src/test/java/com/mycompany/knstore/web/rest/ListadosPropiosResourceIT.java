package com.mycompany.knstore.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.repository.UserRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests para los listados propios de CLIENTE (RF-028): el cliente
 * solo ve los Envios y Facturas de sus propios pedidos, con paginacion real en
 * lote (findByPedidoIdIn/findByPagoIdIn) y sin N+1.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ListadosPropiosResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private User userPropio;

    private Cuenta cuenta;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        userPropio = new User();
        userPropio.setId("user-listados-propios");
        userPropio.setLogin("cliente-listados-propios");
        userPropio.setPassword("x".repeat(60));
        userPropio.setActivated(true);
        userPropio.setEmail("listados-propios@test.com");
        userRepository.save(userPropio);

        cuenta = new Cuenta();
        cuenta.setNumDocumento("1234567890");
        cuenta.setPrimerNombre("Lista");
        cuenta.setSegundoNombre("De");
        cuenta.setPrimerApellido("Items");
        cuenta.setSegundoApellido("Propios");
        cuenta.setGenero(com.mycompany.knstore.domain.enumeration.Genero.FEMENINO);
        cuenta.setFechaNacimiento(java.time.LocalDate.of(1991, 2, 3));
        cuenta.setCelular("3005551122");
        cuenta.setTelefono("6015551122");
        com.mycompany.knstore.domain.TipoDocumento tipoDocumento = new com.mycompany.knstore.domain.TipoDocumento();
        tipoDocumento.setId("tipo-doc-listados");
        tipoDocumento.setNombreTipo("CC");
        tipoDocumento.setSigla("CC");
        tipoDocumento.setEstado(com.mycompany.knstore.domain.enumeration.EstadoTipoDocumento.ACTIVO);
        mongoTemplate.save(tipoDocumento);
        cuenta.setTipoDocumento(tipoDocumento);
        cuenta.setActivo(true);
        cuenta.setUser(userPropio);
        cuenta = cuentaRepository.save(cuenta);

        Direccion direccion = new Direccion();
        direccion.setDireccion("Calle 11 #12-34");
        direccion.setMunicipio("Medellin");
        direccion.setDepartamento("Antioquia");
        direccion.setActivo(true);
        direccion.setTelefonoContacto("3005551122");
        direccion.setDestinatario("Lista Propios");
        direccion.setCodigoPostal("050001");
        direccion.setCuenta(cuenta);
        direccion = direccionRepository.save(direccion);

        pedido = new Pedido();
        pedido.setNumeroPedido("PED-LISTADOS-000001");
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
        envioRepository.deleteAll();
        pagoRepository.deleteAll();
        pedidoRepository.deleteAll();
        direccionRepository.deleteAll();
        cuentaRepository.deleteAll();
        userRepository.deleteAll();
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

    private Cuenta contarOtraCuenta() {
        Cuenta otraCuenta = new Cuenta();
        otraCuenta.setNumDocumento("9876543210");
        otraCuenta.setPrimerNombre("Otra");
        otraCuenta.setSegundoNombre("Cuenta");
        otraCuenta.setPrimerApellido("De");
        otraCuenta.setSegundoApellido("Prueba");
        otraCuenta.setGenero(com.mycompany.knstore.domain.enumeration.Genero.MASCULINO);
        otraCuenta.setFechaNacimiento(java.time.LocalDate.of(1989, 11, 21));
        otraCuenta.setCelular("3006667788");
        otraCuenta.setTelefono("6016667788");
        otraCuenta.setTipoDocumento(cuenta.getTipoDocumento());
        otraCuenta.setActivo(true);
        return cuentaRepository.save(otraCuenta);
    }

    private Pedido guardarPedidoDe(Cuenta c, String numero, String direccionId) {
        Pedido p = new Pedido();
        p.setNumeroPedido(numero);
        p.setEstado(EstadoPedido.PENDING);
        p.setSubtotal(new BigDecimal("50000.00"));
        p.setIvaTotal(new BigDecimal("9500.00"));
        p.setCostoEnvio(BigDecimal.ZERO);
        p.setDescuento(BigDecimal.ZERO);
        p.setTotal(new BigDecimal("59500.00"));
        p.setDireccion(direccionId != null ? direccionRepository.findById(direccionId).orElse(null) : null);
        p.setCuenta(c);
        return pedidoRepository.save(p);
    }

    @Test
    void clienteVeSoloSusEnviosEnElListado() throws Exception {
        Envio envioPropio = new Envio();
        envioPropio.setEstado(EstadoEnvio.PENDING);
        envioPropio.setPedido(pedido);
        envioPropio = envioRepository.save(envioPropio);

        Cuenta otraCuenta = contarOtraCuenta();
        Pedido pedidoAjeno = guardarPedidoDe(otraCuenta, "PED-LISTADOS-000002", null);
        Envio envioAjeno = new Envio();
        envioAjeno.setEstado(EstadoEnvio.PENDING);
        envioAjeno.setPedido(pedidoAjeno);
        envioRepository.save(envioAjeno);

        autenticarComo(userPropio.getId(), AuthoritiesConstants.CLIENTE);

        mockMvc
            .perform(get("/api/envios").param("page", "0").param("size", "10").param("sort", "id,asc"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(jsonPath("$[0].id").value(envioPropio.getId()));
    }

    @Test
    void clienteVeSoloSusFacturasEnElListado() throws Exception {
        Pago pagoPropio = new Pago();
        pagoPropio.setEstado(EstadoPago.APPROVED);
        pagoPropio.setMonto(new BigDecimal("128900.00"));
        pagoPropio.setMetodoPago(MetodoPago.NEQUI);
        pagoPropio.setReferenciaPasarela("SIM-LISTADOS-FACTURA");
        pagoPropio.setPedido(pedido);
        pagoPropio = pagoRepository.save(pagoPropio);

        Factura facturaPropia = new Factura();
        facturaPropia.setSubtotal(new BigDecimal("100000.00"));
        facturaPropia.setTotal(new BigDecimal("128900.00"));
        facturaPropia.setEnviada(true);
        facturaPropia.setPago(pagoPropio);
        facturaPropia = facturaRepository.save(facturaPropia);

        Cuenta otraCuenta = contarOtraCuenta();
        Pedido pedidoAjeno = guardarPedidoDe(otraCuenta, "PED-LISTADOS-000002", null);
        Pago pagoAjeno = new Pago();
        pagoAjeno.setEstado(EstadoPago.APPROVED);
        pagoAjeno.setMonto(new BigDecimal("59500.00"));
        pagoAjeno.setMetodoPago(MetodoPago.NEQUI);
        pagoAjeno.setReferenciaPasarela("SIM-LISTADOS-AJENA");
        pagoAjeno.setPedido(pedidoAjeno);
        pagoAjeno = pagoRepository.save(pagoAjeno);

        Factura facturaAjena = new Factura();
        facturaAjena.setSubtotal(new BigDecimal("50000.00"));
        facturaAjena.setTotal(new BigDecimal("59500.00"));
        facturaAjena.setEnviada(true);
        facturaAjena.setPago(pagoAjeno);
        facturaRepository.save(facturaAjena);

        autenticarComo(userPropio.getId(), AuthoritiesConstants.CLIENTE);

        mockMvc
            .perform(get("/api/facturas").param("page", "0").param("size", "10").param("sort", "id,asc"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(jsonPath("$[0].id").value(facturaPropia.getId()));
    }
}
