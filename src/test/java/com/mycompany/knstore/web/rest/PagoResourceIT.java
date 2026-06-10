package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.PagoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.mapper.PagoMapper;
import java.math.BigDecimal;
import java.time.Instant;
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
 * Integration tests for the {@link PagoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PagoResourceIT {

    private static final MetodoPago DEFAULT_METODO_PAGO = MetodoPago.CREDIT_CARD;
    private static final MetodoPago UPDATED_METODO_PAGO = MetodoPago.DEBIT_CARD;

    private static final EstadoPago DEFAULT_ESTADO = EstadoPago.PENDING;
    private static final EstadoPago UPDATED_ESTADO = EstadoPago.APPROVED;

    private static final BigDecimal DEFAULT_MONTO = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTO = new BigDecimal(1);

    private static final String DEFAULT_REFERENCIA_PASARELA = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCIA_PASARELA = "BBBBBBBBBB";

    private static final String DEFAULT_CODIGO_AUTORIZACION = "AAAAAAAAAA";
    private static final String UPDATED_CODIGO_AUTORIZACION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION_RESPUESTA = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION_RESPUESTA = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_PAGO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_PAGO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_INTENTOS = 0;
    private static final Integer UPDATED_INTENTOS = 1;

    private static final String ENTITY_API_URL = "/api/pagos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PagoMapper pagoMapper;

    @Autowired
    private MockMvc restPagoMockMvc;

    private Pago pago;

    private Pago insertedPago;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pago createEntity() {
        Pago pago = new Pago()
            .metodoPago(DEFAULT_METODO_PAGO)
            .estado(DEFAULT_ESTADO)
            .monto(DEFAULT_MONTO)
            .referenciaPasarela(DEFAULT_REFERENCIA_PASARELA)
            .codigoAutorizacion(DEFAULT_CODIGO_AUTORIZACION)
            .descripcionRespuesta(DEFAULT_DESCRIPCION_RESPUESTA)
            .fechaPago(DEFAULT_FECHA_PAGO)
            .intentos(DEFAULT_INTENTOS);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createEntity();
        pedido.setId("fixed-id-for-tests");
        pago.setPedido(pedido);
        return pago;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pago createUpdatedEntity() {
        Pago updatedPago = new Pago()
            .metodoPago(UPDATED_METODO_PAGO)
            .estado(UPDATED_ESTADO)
            .monto(UPDATED_MONTO)
            .referenciaPasarela(UPDATED_REFERENCIA_PASARELA)
            .codigoAutorizacion(UPDATED_CODIGO_AUTORIZACION)
            .descripcionRespuesta(UPDATED_DESCRIPCION_RESPUESTA)
            .fechaPago(UPDATED_FECHA_PAGO)
            .intentos(UPDATED_INTENTOS);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createUpdatedEntity();
        pedido.setId("fixed-id-for-tests");
        updatedPago.setPedido(pedido);
        return updatedPago;
    }

    @BeforeEach
    void initTest() {
        pago = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPago != null) {
            pagoRepository.delete(insertedPago);
            insertedPago = null;
        }
    }

    @Test
    void createPago() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Pago
        PagoDTO pagoDTO = pagoMapper.toDto(pago);
        var returnedPagoDTO = om.readValue(
            restPagoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PagoDTO.class
        );

        // Validate the Pago in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPago = pagoMapper.toEntity(returnedPagoDTO);
        assertPagoUpdatableFieldsEquals(returnedPago, getPersistedPago(returnedPago));

        insertedPago = returnedPago;
    }

    @Test
    void createPagoWithExistingId() throws Exception {
        // Create the Pago with an existing ID
        pago.setId("existing_id");
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPagoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkMetodoPagoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pago.setMetodoPago(null);

        // Create the Pago, which fails.
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        restPagoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pago.setEstado(null);

        // Create the Pago, which fails.
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        restPagoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMontoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pago.setMonto(null);

        // Create the Pago, which fails.
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        restPagoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPagos() throws Exception {
        // Initialize the database
        insertedPago = pagoRepository.save(pago);

        // Get all the pagoList
        restPagoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pago.getId())))
            .andExpect(jsonPath("$.[*].metodoPago").value(hasItem(DEFAULT_METODO_PAGO.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].monto").value(hasItem(sameNumber(DEFAULT_MONTO))))
            .andExpect(jsonPath("$.[*].referenciaPasarela").value(hasItem(DEFAULT_REFERENCIA_PASARELA)))
            .andExpect(jsonPath("$.[*].codigoAutorizacion").value(hasItem(DEFAULT_CODIGO_AUTORIZACION)))
            .andExpect(jsonPath("$.[*].descripcionRespuesta").value(hasItem(DEFAULT_DESCRIPCION_RESPUESTA)))
            .andExpect(jsonPath("$.[*].fechaPago").value(hasItem(DEFAULT_FECHA_PAGO.toString())))
            .andExpect(jsonPath("$.[*].intentos").value(hasItem(DEFAULT_INTENTOS)));
    }

    @Test
    void getPago() throws Exception {
        // Initialize the database
        insertedPago = pagoRepository.save(pago);

        // Get the pago
        restPagoMockMvc
            .perform(get(ENTITY_API_URL_ID, pago.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pago.getId()))
            .andExpect(jsonPath("$.metodoPago").value(DEFAULT_METODO_PAGO.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.monto").value(sameNumber(DEFAULT_MONTO)))
            .andExpect(jsonPath("$.referenciaPasarela").value(DEFAULT_REFERENCIA_PASARELA))
            .andExpect(jsonPath("$.codigoAutorizacion").value(DEFAULT_CODIGO_AUTORIZACION))
            .andExpect(jsonPath("$.descripcionRespuesta").value(DEFAULT_DESCRIPCION_RESPUESTA))
            .andExpect(jsonPath("$.fechaPago").value(DEFAULT_FECHA_PAGO.toString()))
            .andExpect(jsonPath("$.intentos").value(DEFAULT_INTENTOS));
    }

    @Test
    void getNonExistingPago() throws Exception {
        // Get the pago
        restPagoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPago() throws Exception {
        // Initialize the database
        insertedPago = pagoRepository.save(pago);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pago
        Pago updatedPago = pagoRepository.findById(pago.getId()).orElseThrow();
        updatedPago
            .metodoPago(UPDATED_METODO_PAGO)
            .estado(UPDATED_ESTADO)
            .monto(UPDATED_MONTO)
            .referenciaPasarela(UPDATED_REFERENCIA_PASARELA)
            .codigoAutorizacion(UPDATED_CODIGO_AUTORIZACION)
            .descripcionRespuesta(UPDATED_DESCRIPCION_RESPUESTA)
            .fechaPago(UPDATED_FECHA_PAGO)
            .intentos(UPDATED_INTENTOS);
        PagoDTO pagoDTO = pagoMapper.toDto(updatedPago);

        restPagoMockMvc
            .perform(put(ENTITY_API_URL_ID, pagoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isOk());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPagoToMatchAllProperties(updatedPago);
    }

    @Test
    void putNonExistingPago() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pago.setId(UUID.randomUUID().toString());

        // Create the Pago
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPagoMockMvc
            .perform(put(ENTITY_API_URL_ID, pagoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPago() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pago.setId(UUID.randomUUID().toString());

        // Create the Pago
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPagoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pagoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPago() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pago.setId(UUID.randomUUID().toString());

        // Create the Pago
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPagoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePagoWithPatch() throws Exception {
        // Initialize the database
        insertedPago = pagoRepository.save(pago);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pago using partial update
        Pago partialUpdatedPago = new Pago();
        partialUpdatedPago.setId(pago.getId());

        partialUpdatedPago
            .estado(UPDATED_ESTADO)
            .referenciaPasarela(UPDATED_REFERENCIA_PASARELA)
            .codigoAutorizacion(UPDATED_CODIGO_AUTORIZACION)
            .descripcionRespuesta(UPDATED_DESCRIPCION_RESPUESTA)
            .fechaPago(UPDATED_FECHA_PAGO);

        restPagoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPago.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPago))
            )
            .andExpect(status().isOk());

        // Validate the Pago in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPagoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPago, pago), getPersistedPago(pago));
    }

    @Test
    void fullUpdatePagoWithPatch() throws Exception {
        // Initialize the database
        insertedPago = pagoRepository.save(pago);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pago using partial update
        Pago partialUpdatedPago = new Pago();
        partialUpdatedPago.setId(pago.getId());

        partialUpdatedPago
            .metodoPago(UPDATED_METODO_PAGO)
            .estado(UPDATED_ESTADO)
            .monto(UPDATED_MONTO)
            .referenciaPasarela(UPDATED_REFERENCIA_PASARELA)
            .codigoAutorizacion(UPDATED_CODIGO_AUTORIZACION)
            .descripcionRespuesta(UPDATED_DESCRIPCION_RESPUESTA)
            .fechaPago(UPDATED_FECHA_PAGO)
            .intentos(UPDATED_INTENTOS);

        restPagoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPago.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPago))
            )
            .andExpect(status().isOk());

        // Validate the Pago in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPagoUpdatableFieldsEquals(partialUpdatedPago, getPersistedPago(partialUpdatedPago));
    }

    @Test
    void patchNonExistingPago() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pago.setId(UUID.randomUUID().toString());

        // Create the Pago
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPagoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pagoDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pagoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPago() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pago.setId(UUID.randomUUID().toString());

        // Create the Pago
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPagoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pagoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPago() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pago.setId(UUID.randomUUID().toString());

        // Create the Pago
        PagoDTO pagoDTO = pagoMapper.toDto(pago);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPagoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pagoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pago in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePago() throws Exception {
        // Initialize the database
        insertedPago = pagoRepository.save(pago);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pago
        restPagoMockMvc
            .perform(delete(ENTITY_API_URL_ID, pago.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pagoRepository.count();
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

    protected Pago getPersistedPago(Pago pago) {
        return pagoRepository.findById(pago.getId()).orElseThrow();
    }

    protected void assertPersistedPagoToMatchAllProperties(Pago expectedPago) {
        assertPagoAllPropertiesEquals(expectedPago, getPersistedPago(expectedPago));
    }

    protected void assertPersistedPagoToMatchUpdatableProperties(Pago expectedPago) {
        assertPagoAllUpdatablePropertiesEquals(expectedPago, getPersistedPago(expectedPago));
    }
}
