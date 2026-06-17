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
import com.mycompany.knstore.domain.Pago;
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

    private static final String DEFAULT_PREFIJO = "AAAAAAAAAA";
    private static final String UPDATED_PREFIJO = "BBBBBBBBBB";

    private static final String DEFAULT_CUFE = "AAAAAAAAAA";
    private static final String UPDATED_CUFE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);

    private static final BigDecimal DEFAULT_DESCUENTOS = new BigDecimal(0);
    private static final BigDecimal UPDATED_DESCUENTOS = new BigDecimal(1);

    private static final BigDecimal DEFAULT_BASE_GRAVABLE_IVA = new BigDecimal(0);
    private static final BigDecimal UPDATED_BASE_GRAVABLE_IVA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_VALOR_IVA = new BigDecimal(0);
    private static final BigDecimal UPDATED_VALOR_IVA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(1);

    private static final String DEFAULT_NOTAS_ADICIONALES = "AAAAAAAAAA";
    private static final String UPDATED_NOTAS_ADICIONALES = "BBBBBBBBBB";

    private static final String DEFAULT_CODIGO_QR = "AAAAAAAAAA";
    private static final String UPDATED_CODIGO_QR = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENVIADA = false;
    private static final Boolean UPDATED_ENVIADA = true;

    private static final Instant DEFAULT_FECHA_EMISION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_EMISION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LocalDate DEFAULT_FECHA_VENCIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_VENCIMIENTO = LocalDate.now(ZoneId.systemDefault());

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
            .prefijo(DEFAULT_PREFIJO)
            .cufe(DEFAULT_CUFE)
            .subtotal(DEFAULT_SUBTOTAL)
            .descuentos(DEFAULT_DESCUENTOS)
            .baseGravableIva(DEFAULT_BASE_GRAVABLE_IVA)
            .valorIva(DEFAULT_VALOR_IVA)
            .total(DEFAULT_TOTAL)
            .notasAdicionales(DEFAULT_NOTAS_ADICIONALES)
            .codigoQr(DEFAULT_CODIGO_QR)
            .enviada(DEFAULT_ENVIADA)
            .fechaEmision(DEFAULT_FECHA_EMISION)
            .fechaVencimiento(DEFAULT_FECHA_VENCIMIENTO)
            .fechaEnvioEmail(DEFAULT_FECHA_ENVIO_EMAIL);
        // Add required entity
        Pago pago;
        pago = PagoResourceIT.createEntity();
        pago.setId("fixed-id-for-tests");
        factura.setPago(pago);
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
            .prefijo(UPDATED_PREFIJO)
            .cufe(UPDATED_CUFE)
            .subtotal(UPDATED_SUBTOTAL)
            .descuentos(UPDATED_DESCUENTOS)
            .baseGravableIva(UPDATED_BASE_GRAVABLE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .total(UPDATED_TOTAL)
            .notasAdicionales(UPDATED_NOTAS_ADICIONALES)
            .codigoQr(UPDATED_CODIGO_QR)
            .enviada(UPDATED_ENVIADA)
            .fechaEmision(UPDATED_FECHA_EMISION)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO)
            .fechaEnvioEmail(UPDATED_FECHA_ENVIO_EMAIL);
        // Add required entity
        Pago pago;
        pago = PagoResourceIT.createUpdatedEntity();
        pago.setId("fixed-id-for-tests");
        updatedFactura.setPago(pago);
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
            .andExpect(jsonPath("$.[*].prefijo").value(hasItem(DEFAULT_PREFIJO)))
            .andExpect(jsonPath("$.[*].cufe").value(hasItem(DEFAULT_CUFE)))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].descuentos").value(hasItem(sameNumber(DEFAULT_DESCUENTOS))))
            .andExpect(jsonPath("$.[*].baseGravableIva").value(hasItem(sameNumber(DEFAULT_BASE_GRAVABLE_IVA))))
            .andExpect(jsonPath("$.[*].valorIva").value(hasItem(sameNumber(DEFAULT_VALOR_IVA))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].notasAdicionales").value(hasItem(DEFAULT_NOTAS_ADICIONALES)))
            .andExpect(jsonPath("$.[*].codigoQr").value(hasItem(DEFAULT_CODIGO_QR)))
            .andExpect(jsonPath("$.[*].enviada").value(hasItem(DEFAULT_ENVIADA)))
            .andExpect(jsonPath("$.[*].fechaEmision").value(hasItem(DEFAULT_FECHA_EMISION.toString())))
            .andExpect(jsonPath("$.[*].fechaVencimiento").value(hasItem(DEFAULT_FECHA_VENCIMIENTO.toString())))
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
            .andExpect(jsonPath("$.prefijo").value(DEFAULT_PREFIJO))
            .andExpect(jsonPath("$.cufe").value(DEFAULT_CUFE))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.descuentos").value(sameNumber(DEFAULT_DESCUENTOS)))
            .andExpect(jsonPath("$.baseGravableIva").value(sameNumber(DEFAULT_BASE_GRAVABLE_IVA)))
            .andExpect(jsonPath("$.valorIva").value(sameNumber(DEFAULT_VALOR_IVA)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.notasAdicionales").value(DEFAULT_NOTAS_ADICIONALES))
            .andExpect(jsonPath("$.codigoQr").value(DEFAULT_CODIGO_QR))
            .andExpect(jsonPath("$.enviada").value(DEFAULT_ENVIADA))
            .andExpect(jsonPath("$.fechaEmision").value(DEFAULT_FECHA_EMISION.toString()))
            .andExpect(jsonPath("$.fechaVencimiento").value(DEFAULT_FECHA_VENCIMIENTO.toString()))
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
            .prefijo(UPDATED_PREFIJO)
            .cufe(UPDATED_CUFE)
            .subtotal(UPDATED_SUBTOTAL)
            .descuentos(UPDATED_DESCUENTOS)
            .baseGravableIva(UPDATED_BASE_GRAVABLE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .total(UPDATED_TOTAL)
            .notasAdicionales(UPDATED_NOTAS_ADICIONALES)
            .codigoQr(UPDATED_CODIGO_QR)
            .enviada(UPDATED_ENVIADA)
            .fechaEmision(UPDATED_FECHA_EMISION)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO)
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
            .baseGravableIva(UPDATED_BASE_GRAVABLE_IVA)
            .total(UPDATED_TOTAL)
            .notasAdicionales(UPDATED_NOTAS_ADICIONALES)
            .enviada(UPDATED_ENVIADA)
            .fechaEmision(UPDATED_FECHA_EMISION)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO);

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
            .prefijo(UPDATED_PREFIJO)
            .cufe(UPDATED_CUFE)
            .subtotal(UPDATED_SUBTOTAL)
            .descuentos(UPDATED_DESCUENTOS)
            .baseGravableIva(UPDATED_BASE_GRAVABLE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .total(UPDATED_TOTAL)
            .notasAdicionales(UPDATED_NOTAS_ADICIONALES)
            .codigoQr(UPDATED_CODIGO_QR)
            .enviada(UPDATED_ENVIADA)
            .fechaEmision(UPDATED_FECHA_EMISION)
            .fechaVencimiento(UPDATED_FECHA_VENCIMIENTO)
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
