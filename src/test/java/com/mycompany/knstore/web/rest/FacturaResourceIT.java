package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.FacturaAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.mapper.FacturaMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link FacturaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FacturaResourceIT {

    private static final String DEFAULT_REFERENCIA = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCIA = "BBBBBBBBBB";

    private static final String DEFAULT_CUFE = "AAAAAAAAAA";
    private static final String UPDATED_CUFE = "BBBBBBBBBB";

    private static final String DEFAULT_RESOLUCION_DIAN = "AAAAAAAAAA";
    private static final String UPDATED_RESOLUCION_DIAN = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FECHA_VIGENCIA_RESOLUCION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_VIGENCIA_RESOLUCION = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PREFIJO = "AAAAAAAAAA";
    private static final String UPDATED_PREFIJO = "BBBBBBBBBB";

    private static final Long DEFAULT_CONSECUTIVO = 1L;
    private static final Long UPDATED_CONSECUTIVO = 2L;

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);

    private static final BigDecimal DEFAULT_DESCUENTOS = new BigDecimal(0);
    private static final BigDecimal UPDATED_DESCUENTOS = new BigDecimal(1);

    private static final BigDecimal DEFAULT_BASE_GRAVABLE_IVA = new BigDecimal(0);
    private static final BigDecimal UPDATED_BASE_GRAVABLE_IVA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_VALOR_IVA = new BigDecimal(0);
    private static final BigDecimal UPDATED_VALOR_IVA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_RETEFUENTE = new BigDecimal(0);
    private static final BigDecimal UPDATED_RETEFUENTE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_RETE_IVA = new BigDecimal(0);
    private static final BigDecimal UPDATED_RETE_IVA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_RETE_ICA = new BigDecimal(0);
    private static final BigDecimal UPDATED_RETE_ICA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(1);

    private static final Instant DEFAULT_FECHA_EMISION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_EMISION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LocalDate DEFAULT_FECHA_VENCIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_VENCIMIENTO = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTAS_ADICIONALES = "AAAAAAAAAA";
    private static final String UPDATED_NOTAS_ADICIONALES = "BBBBBBBBBB";

    private static final String DEFAULT_CODIGO_QR = "AAAAAAAAAA";
    private static final String UPDATED_CODIGO_QR = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENVIADA = false;
    private static final Boolean UPDATED_ENVIADA = true;

    private static final Instant DEFAULT_FECHA_ENVIO_EMAIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_ENVIO_EMAIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/facturas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaMapper facturaMapper;

    @Autowired
    private MockMvc restFacturaMockMvc;

    private Factura factura;

    private Factura insertedFactura;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createEntity() {
        Factura factura = new Factura()
            .referencia(DEFAULT_REFERENCIA)
            .cufe(DEFAULT_CUFE)
            .resolucionDian(DEFAULT_RESOLUCION_DIAN)
            .fechaVigenciaResolucion(DEFAULT_FECHA_VIGENCIA_RESOLUCION)
            .prefijo(DEFAULT_PREFIJO)
            .consecutivo(DEFAULT_CONSECUTIVO)
            .subtotal(DEFAULT_SUBTOTAL)
            .descuentos(DEFAULT_DESCUENTOS)
            .baseGravableIva(DEFAULT_BASE_GRAVABLE_IVA)
            .valorIva(DEFAULT_VALOR_IVA)
            .retefuente(DEFAULT_RETEFUENTE)
            .reteIva(DEFAULT_RETE_IVA)
            .reteIca(DEFAULT_RETE_ICA)
            .total(DEFAULT_TOTAL)
            .fechaEmision(DEFAULT_FECHA_EMISION)
            .fechaVencimiento(DEFAULT_FECHA_VENCIMIENTO)
            .notasAdicionales(DEFAULT_NOTAS_ADICIONALES)
            .codigoQr(DEFAULT_CODIGO_QR)
            .enviada(DEFAULT_ENVIADA)
            .fechaEnvioEmail(DEFAULT_FECHA_ENVIO_EMAIL);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createEntity();
        pedido.setId("fixed-id-for-tests");
        factura.setPedido(pedido);
        return factura;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createUpdatedEntity() {
        Factura updatedFactura = new Factura()
            .referencia(UPDATED_REFERENCIA)
            .cufe(UPDATED_CUFE)
            .resolucionDian(UPDATED_RESOLUCION_DIAN)
            .fechaVigenciaResolucion(UPDATED_FECHA_VIGENCIA_RESOLUCION)
            .prefijo(UPDATED_PREFIJO)
            .consecutivo(UPDATED_CONSECUTIVO)
            .subtotal(UPDATED_SUBTOTAL)
            .descuentos(UPDATED_DESCUENTOS)
            .baseGravableIva(UPDATED_BASE_GRAVABLE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .retefuente(UPDATED_RETEFUENTE)
            .reteIva(UPDATED_RETE_IVA)
            .reteIca(UPDATED_RETE_ICA)
            .total(UPDATED_TOTAL)
            .fechaEmision(UPDATED_FECHA_EMISION)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO)
            .notasAdicionales(UPDATED_NOTAS_ADICIONALES)
            .codigoQr(UPDATED_CODIGO_QR)
            .enviada(UPDATED_ENVIADA)
            .fechaEnvioEmail(UPDATED_FECHA_ENVIO_EMAIL);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createUpdatedEntity();
        pedido.setId("fixed-id-for-tests");
        updatedFactura.setPedido(pedido);
        return updatedFactura;
    }

    @BeforeEach
    void initTest() {
        factura = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFactura != null) {
            facturaRepository.delete(insertedFactura);
            insertedFactura = null;
        }
    }

    @Test
    void createFactura() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);
        var returnedFacturaDTO = om.readValue(
            restFacturaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FacturaDTO.class
        );

        // Validate the Factura in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFactura = facturaMapper.toEntity(returnedFacturaDTO);
        assertFacturaUpdatableFieldsEquals(returnedFactura, getPersistedFactura(returnedFactura));

        insertedFactura = returnedFactura;
    }

    @Test
    void createFacturaWithExistingId() throws Exception {
        // Create the Factura with an existing ID
        factura.setId("existing_id");
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenciaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        factura.setReferencia(null);

        // Create the Factura, which fails.
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        factura.setSubtotal(null);

        // Create the Factura, which fails.
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        factura.setTotal(null);

        // Create the Factura, which fails.
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEnviadaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        factura.setEnviada(null);

        // Create the Factura, which fails.
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllFacturas() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.save(factura);

        // Get all the facturaList
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(factura.getId())))
            .andExpect(jsonPath("$.[*].referencia").value(hasItem(DEFAULT_REFERENCIA)))
            .andExpect(jsonPath("$.[*].cufe").value(hasItem(DEFAULT_CUFE)))
            .andExpect(jsonPath("$.[*].resolucionDian").value(hasItem(DEFAULT_RESOLUCION_DIAN)))
            .andExpect(jsonPath("$.[*].fechaVigenciaResolucion").value(hasItem(DEFAULT_FECHA_VIGENCIA_RESOLUCION.toString())))
            .andExpect(jsonPath("$.[*].prefijo").value(hasItem(DEFAULT_PREFIJO)))
            .andExpect(jsonPath("$.[*].consecutivo").value(hasItem(DEFAULT_CONSECUTIVO.intValue())))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].descuentos").value(hasItem(sameNumber(DEFAULT_DESCUENTOS))))
            .andExpect(jsonPath("$.[*].baseGravableIva").value(hasItem(sameNumber(DEFAULT_BASE_GRAVABLE_IVA))))
            .andExpect(jsonPath("$.[*].valorIva").value(hasItem(sameNumber(DEFAULT_VALOR_IVA))))
            .andExpect(jsonPath("$.[*].retefuente").value(hasItem(sameNumber(DEFAULT_RETEFUENTE))))
            .andExpect(jsonPath("$.[*].reteIva").value(hasItem(sameNumber(DEFAULT_RETE_IVA))))
            .andExpect(jsonPath("$.[*].reteIca").value(hasItem(sameNumber(DEFAULT_RETE_ICA))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].fechaEmision").value(hasItem(DEFAULT_FECHA_EMISION.toString())))
            .andExpect(jsonPath("$.[*].fechaVencimiento").value(hasItem(DEFAULT_FECHA_VENCIMIENTO.toString())))
            .andExpect(jsonPath("$.[*].notasAdicionales").value(hasItem(DEFAULT_NOTAS_ADICIONALES)))
            .andExpect(jsonPath("$.[*].codigoQr").value(hasItem(DEFAULT_CODIGO_QR)))
            .andExpect(jsonPath("$.[*].enviada").value(hasItem(DEFAULT_ENVIADA)))
            .andExpect(jsonPath("$.[*].fechaEnvioEmail").value(hasItem(DEFAULT_FECHA_ENVIO_EMAIL.toString())));
    }

    @Test
    void getFactura() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.save(factura);

        // Get the factura
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL_ID, factura.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(factura.getId()))
            .andExpect(jsonPath("$.referencia").value(DEFAULT_REFERENCIA))
            .andExpect(jsonPath("$.cufe").value(DEFAULT_CUFE))
            .andExpect(jsonPath("$.resolucionDian").value(DEFAULT_RESOLUCION_DIAN))
            .andExpect(jsonPath("$.fechaVigenciaResolucion").value(DEFAULT_FECHA_VIGENCIA_RESOLUCION.toString()))
            .andExpect(jsonPath("$.prefijo").value(DEFAULT_PREFIJO))
            .andExpect(jsonPath("$.consecutivo").value(DEFAULT_CONSECUTIVO.intValue()))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.descuentos").value(sameNumber(DEFAULT_DESCUENTOS)))
            .andExpect(jsonPath("$.baseGravableIva").value(sameNumber(DEFAULT_BASE_GRAVABLE_IVA)))
            .andExpect(jsonPath("$.valorIva").value(sameNumber(DEFAULT_VALOR_IVA)))
            .andExpect(jsonPath("$.retefuente").value(sameNumber(DEFAULT_RETEFUENTE)))
            .andExpect(jsonPath("$.reteIva").value(sameNumber(DEFAULT_RETE_IVA)))
            .andExpect(jsonPath("$.reteIca").value(sameNumber(DEFAULT_RETE_ICA)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.fechaEmision").value(DEFAULT_FECHA_EMISION.toString()))
            .andExpect(jsonPath("$.fechaVencimiento").value(DEFAULT_FECHA_VENCIMIENTO.toString()))
            .andExpect(jsonPath("$.notasAdicionales").value(DEFAULT_NOTAS_ADICIONALES))
            .andExpect(jsonPath("$.codigoQr").value(DEFAULT_CODIGO_QR))
            .andExpect(jsonPath("$.enviada").value(DEFAULT_ENVIADA))
            .andExpect(jsonPath("$.fechaEnvioEmail").value(DEFAULT_FECHA_ENVIO_EMAIL.toString()));
    }

    @Test
    void getNonExistingFactura() throws Exception {
        // Get the factura
        restFacturaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingFactura() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.save(factura);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the factura
        Factura updatedFactura = facturaRepository.findById(factura.getId()).orElseThrow();
        updatedFactura
            .referencia(UPDATED_REFERENCIA)
            .cufe(UPDATED_CUFE)
            .resolucionDian(UPDATED_RESOLUCION_DIAN)
            .fechaVigenciaResolucion(UPDATED_FECHA_VIGENCIA_RESOLUCION)
            .prefijo(UPDATED_PREFIJO)
            .consecutivo(UPDATED_CONSECUTIVO)
            .subtotal(UPDATED_SUBTOTAL)
            .descuentos(UPDATED_DESCUENTOS)
            .baseGravableIva(UPDATED_BASE_GRAVABLE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .retefuente(UPDATED_RETEFUENTE)
            .reteIva(UPDATED_RETE_IVA)
            .reteIca(UPDATED_RETE_ICA)
            .total(UPDATED_TOTAL)
            .fechaEmision(UPDATED_FECHA_EMISION)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO)
            .notasAdicionales(UPDATED_NOTAS_ADICIONALES)
            .codigoQr(UPDATED_CODIGO_QR)
            .enviada(UPDATED_ENVIADA)
            .fechaEnvioEmail(UPDATED_FECHA_ENVIO_EMAIL);
        FacturaDTO facturaDTO = facturaMapper.toDto(updatedFactura);

        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facturaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFacturaToMatchAllProperties(updatedFactura);
    }

    @Test
    void putNonExistingFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(UUID.randomUUID().toString());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facturaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(UUID.randomUUID().toString());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(UUID.randomUUID().toString());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(facturaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.save(factura);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura
            .cufe(UPDATED_CUFE)
            .prefijo(UPDATED_PREFIJO)
            .subtotal(UPDATED_SUBTOTAL)
            .descuentos(UPDATED_DESCUENTOS)
            .valorIva(UPDATED_VALOR_IVA)
            .retefuente(UPDATED_RETEFUENTE)
            .reteIva(UPDATED_RETE_IVA)
            .total(UPDATED_TOTAL)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO)
            .enviada(UPDATED_ENVIADA);

        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFactura))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFacturaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFactura, factura), getPersistedFactura(factura));
    }

    @Test
    void fullUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.save(factura);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura
            .referencia(UPDATED_REFERENCIA)
            .cufe(UPDATED_CUFE)
            .resolucionDian(UPDATED_RESOLUCION_DIAN)
            .fechaVigenciaResolucion(UPDATED_FECHA_VIGENCIA_RESOLUCION)
            .prefijo(UPDATED_PREFIJO)
            .consecutivo(UPDATED_CONSECUTIVO)
            .subtotal(UPDATED_SUBTOTAL)
            .descuentos(UPDATED_DESCUENTOS)
            .baseGravableIva(UPDATED_BASE_GRAVABLE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .retefuente(UPDATED_RETEFUENTE)
            .reteIva(UPDATED_RETE_IVA)
            .reteIca(UPDATED_RETE_ICA)
            .total(UPDATED_TOTAL)
            .fechaEmision(UPDATED_FECHA_EMISION)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO)
            .notasAdicionales(UPDATED_NOTAS_ADICIONALES)
            .codigoQr(UPDATED_CODIGO_QR)
            .enviada(UPDATED_ENVIADA)
            .fechaEnvioEmail(UPDATED_FECHA_ENVIO_EMAIL);

        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFactura))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFacturaUpdatableFieldsEquals(partialUpdatedFactura, getPersistedFactura(partialUpdatedFactura));
    }

    @Test
    void patchNonExistingFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(UUID.randomUUID().toString());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, facturaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(UUID.randomUUID().toString());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFactura() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        factura.setId(UUID.randomUUID().toString());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(facturaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factura in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFactura() throws Exception {
        // Initialize the database
        insertedFactura = facturaRepository.save(factura);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the factura
        restFacturaMockMvc
            .perform(delete(ENTITY_API_URL_ID, factura.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return facturaRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Factura getPersistedFactura(Factura factura) {
        return facturaRepository.findById(factura.getId()).orElseThrow();
    }

    protected void assertPersistedFacturaToMatchAllProperties(Factura expectedFactura) {
        assertFacturaAllPropertiesEquals(expectedFactura, getPersistedFactura(expectedFactura));
    }

    protected void assertPersistedFacturaToMatchUpdatableProperties(Factura expectedFactura) {
        assertFacturaAllUpdatablePropertiesEquals(expectedFactura, getPersistedFactura(expectedFactura));
    }
}
