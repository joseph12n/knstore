package com.mycompany.knstore.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
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
    private MongoTemplate mongoTemplate;

    private Cuenta cuenta;

    private Direccion direccion;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setNumDocumento("DOC-FLUJO");
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
        facturaRepository.deleteAll();
        pagoRepository.deleteAll();
        pedidoRepository.deleteAll();
        direccionRepository.deleteAll();
        cuentaRepository.deleteAll();
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
}
