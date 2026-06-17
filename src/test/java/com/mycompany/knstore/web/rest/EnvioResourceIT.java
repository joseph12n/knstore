package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.EnvioAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.TipoServicioEnvio;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
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
 * Integration tests for the {@link EnvioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnvioResourceIT {

    private static final String DEFAULT_TRANSPORTADORA = "AAAAAAAAAA";
    private static final String UPDATED_TRANSPORTADORA = "BBBBBBBBBB";

    private static final String DEFAULT_NUMERO_RASTREO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_RASTREO = "BBBBBBBBBB";

    private static final TipoServicioEnvio DEFAULT_TIPO_SERVICIO = TipoServicioEnvio.ESTANDAR;
    private static final TipoServicioEnvio UPDATED_TIPO_SERVICIO = TipoServicioEnvio.EXPRESS;

    private static final EstadoEnvio DEFAULT_ESTADO = EstadoEnvio.PENDING;
    private static final EstadoEnvio UPDATED_ESTADO = EstadoEnvio.DISPATCHED;

    private static final BigDecimal DEFAULT_COSTO_ENVIO = new BigDecimal(0);
    private static final BigDecimal UPDATED_COSTO_ENVIO = new BigDecimal(1);

    private static final BigDecimal DEFAULT_PESO_KG = new BigDecimal(0);
    private static final BigDecimal UPDATED_PESO_KG = new BigDecimal(1);

    private static final BigDecimal DEFAULT_VALOR_DECLARADO = new BigDecimal(0);
    private static final BigDecimal UPDATED_VALOR_DECLARADO = new BigDecimal(1);

    private static final String DEFAULT_URL_RASTREO = "AAAAAAAAAA";
    private static final String UPDATED_URL_RASTREO = "BBBBBBBBBB";

    private static final String DEFAULT_OBSERVACIONES = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACIONES = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_DESPACHO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_DESPACHO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FECHA_ENTREGA_ESTIMADA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_ENTREGA_ESTIMADA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FECHA_ENTREGA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_ENTREGA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/envios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private EnvioMapper envioMapper;

    @Autowired
    private MockMvc restEnvioMockMvc;

    private Envio envio;

    private Envio insertedEnvio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Envio createEntity() {
        Envio envio = new Envio()
            .transportadora(DEFAULT_TRANSPORTADORA)
            .numeroRastreo(DEFAULT_NUMERO_RASTREO)
            .tipoServicio(DEFAULT_TIPO_SERVICIO)
            .estado(DEFAULT_ESTADO)
            .costoEnvio(DEFAULT_COSTO_ENVIO)
            .pesoKg(DEFAULT_PESO_KG)
            .valorDeclarado(DEFAULT_VALOR_DECLARADO)
            .urlRastreo(DEFAULT_URL_RASTREO)
            .observaciones(DEFAULT_OBSERVACIONES)
            .fechaDespacho(DEFAULT_FECHA_DESPACHO)
            .fechaEntregaEstimada(DEFAULT_FECHA_ENTREGA_ESTIMADA)
            .fechaEntrega(DEFAULT_FECHA_ENTREGA);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createEntity();
        pedido.setId("fixed-id-for-tests");
        envio.setPedido(pedido);
        return envio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Envio createUpdatedEntity() {
        Envio updatedEnvio = new Envio()
            .transportadora(UPDATED_TRANSPORTADORA)
            .numeroRastreo(UPDATED_NUMERO_RASTREO)
            .tipoServicio(UPDATED_TIPO_SERVICIO)
            .estado(UPDATED_ESTADO)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .pesoKg(UPDATED_PESO_KG)
            .valorDeclarado(UPDATED_VALOR_DECLARADO)
            .urlRastreo(UPDATED_URL_RASTREO)
            .observaciones(UPDATED_OBSERVACIONES)
            .fechaDespacho(UPDATED_FECHA_DESPACHO)
            .fechaEntregaEstimada(UPDATED_FECHA_ENTREGA_ESTIMADA)
            .fechaEntrega(UPDATED_FECHA_ENTREGA);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createUpdatedEntity();
        pedido.setId("fixed-id-for-tests");
        updatedEnvio.setPedido(pedido);
        return updatedEnvio;
    }

    @BeforeEach
    void initTest() {
        envio = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEnvio != null) {
            envioRepository.delete(insertedEnvio);
            insertedEnvio = null;
        }
    }

    @Test
    void createEnvio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Envio
        EnvioDTO envioDTO = envioMapper.toDto(envio);
        var returnedEnvioDTO = om.readValue(
            restEnvioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envioDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EnvioDTO.class
        );

        // Validate the Envio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEnvio = envioMapper.toEntity(returnedEnvioDTO);
        assertEnvioUpdatableFieldsEquals(returnedEnvio, getPersistedEnvio(returnedEnvio));

        insertedEnvio = returnedEnvio;
    }

    @Test
    void createEnvioWithExistingId() throws Exception {
        // Create the Envio with an existing ID
        envio.setId("existing_id");
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        envio.setEstado(null);

        // Create the Envio, which fails.
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        restEnvioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEnvios() throws Exception {
        // Initialize the database
        insertedEnvio = envioRepository.save(envio);

        // Get all the envioList
        restEnvioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(envio.getId())))
            .andExpect(jsonPath("$.[*].transportadora").value(hasItem(DEFAULT_TRANSPORTADORA)))
            .andExpect(jsonPath("$.[*].numeroRastreo").value(hasItem(DEFAULT_NUMERO_RASTREO)))
            .andExpect(jsonPath("$.[*].tipoServicio").value(hasItem(DEFAULT_TIPO_SERVICIO.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].costoEnvio").value(hasItem(sameNumber(DEFAULT_COSTO_ENVIO))))
            .andExpect(jsonPath("$.[*].pesoKg").value(hasItem(sameNumber(DEFAULT_PESO_KG))))
            .andExpect(jsonPath("$.[*].valorDeclarado").value(hasItem(sameNumber(DEFAULT_VALOR_DECLARADO))))
            .andExpect(jsonPath("$.[*].urlRastreo").value(hasItem(DEFAULT_URL_RASTREO)))
            .andExpect(jsonPath("$.[*].observaciones").value(hasItem(DEFAULT_OBSERVACIONES)))
            .andExpect(jsonPath("$.[*].fechaDespacho").value(hasItem(DEFAULT_FECHA_DESPACHO.toString())))
            .andExpect(jsonPath("$.[*].fechaEntregaEstimada").value(hasItem(DEFAULT_FECHA_ENTREGA_ESTIMADA.toString())))
            .andExpect(jsonPath("$.[*].fechaEntrega").value(hasItem(DEFAULT_FECHA_ENTREGA.toString())));
    }

    @Test
    void getEnvio() throws Exception {
        // Initialize the database
        insertedEnvio = envioRepository.save(envio);

        // Get the envio
        restEnvioMockMvc
            .perform(get(ENTITY_API_URL_ID, envio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(envio.getId()))
            .andExpect(jsonPath("$.transportadora").value(DEFAULT_TRANSPORTADORA))
            .andExpect(jsonPath("$.numeroRastreo").value(DEFAULT_NUMERO_RASTREO))
            .andExpect(jsonPath("$.tipoServicio").value(DEFAULT_TIPO_SERVICIO.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.costoEnvio").value(sameNumber(DEFAULT_COSTO_ENVIO)))
            .andExpect(jsonPath("$.pesoKg").value(sameNumber(DEFAULT_PESO_KG)))
            .andExpect(jsonPath("$.valorDeclarado").value(sameNumber(DEFAULT_VALOR_DECLARADO)))
            .andExpect(jsonPath("$.urlRastreo").value(DEFAULT_URL_RASTREO))
            .andExpect(jsonPath("$.observaciones").value(DEFAULT_OBSERVACIONES))
            .andExpect(jsonPath("$.fechaDespacho").value(DEFAULT_FECHA_DESPACHO.toString()))
            .andExpect(jsonPath("$.fechaEntregaEstimada").value(DEFAULT_FECHA_ENTREGA_ESTIMADA.toString()))
            .andExpect(jsonPath("$.fechaEntrega").value(DEFAULT_FECHA_ENTREGA.toString()));
    }

    @Test
    void getNonExistingEnvio() throws Exception {
        // Get the envio
        restEnvioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingEnvio() throws Exception {
        // Initialize the database
        insertedEnvio = envioRepository.save(envio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the envio
        Envio updatedEnvio = envioRepository.findById(envio.getId()).orElseThrow();
        updatedEnvio
            .transportadora(UPDATED_TRANSPORTADORA)
            .numeroRastreo(UPDATED_NUMERO_RASTREO)
            .tipoServicio(UPDATED_TIPO_SERVICIO)
            .estado(UPDATED_ESTADO)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .pesoKg(UPDATED_PESO_KG)
            .valorDeclarado(UPDATED_VALOR_DECLARADO)
            .urlRastreo(UPDATED_URL_RASTREO)
            .observaciones(UPDATED_OBSERVACIONES)
            .fechaDespacho(UPDATED_FECHA_DESPACHO)
            .fechaEntregaEstimada(UPDATED_FECHA_ENTREGA_ESTIMADA)
            .fechaEntrega(UPDATED_FECHA_ENTREGA);
        EnvioDTO envioDTO = envioMapper.toDto(updatedEnvio);

        restEnvioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, envioDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envioDTO))
            )
            .andExpect(status().isOk());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnvioToMatchAllProperties(updatedEnvio);
    }

    @Test
    void putNonExistingEnvio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        envio.setId(UUID.randomUUID().toString());

        // Create the Envio
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, envioDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEnvio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        envio.setId(UUID.randomUUID().toString());

        // Create the Envio
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(envioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEnvio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        envio.setId(UUID.randomUUID().toString());

        // Create the Envio
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEnvioWithPatch() throws Exception {
        // Initialize the database
        insertedEnvio = envioRepository.save(envio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the envio using partial update
        Envio partialUpdatedEnvio = new Envio();
        partialUpdatedEnvio.setId(envio.getId());

        partialUpdatedEnvio
            .numeroRastreo(UPDATED_NUMERO_RASTREO)
            .tipoServicio(UPDATED_TIPO_SERVICIO)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .pesoKg(UPDATED_PESO_KG)
            .valorDeclarado(UPDATED_VALOR_DECLARADO)
            .observaciones(UPDATED_OBSERVACIONES)
            .fechaDespacho(UPDATED_FECHA_DESPACHO)
            .fechaEntrega(UPDATED_FECHA_ENTREGA);

        restEnvioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvio))
            )
            .andExpect(status().isOk());

        // Validate the Envio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvioUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEnvio, envio), getPersistedEnvio(envio));
    }

    @Test
    void fullUpdateEnvioWithPatch() throws Exception {
        // Initialize the database
        insertedEnvio = envioRepository.save(envio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the envio using partial update
        Envio partialUpdatedEnvio = new Envio();
        partialUpdatedEnvio.setId(envio.getId());

        partialUpdatedEnvio
            .transportadora(UPDATED_TRANSPORTADORA)
            .numeroRastreo(UPDATED_NUMERO_RASTREO)
            .tipoServicio(UPDATED_TIPO_SERVICIO)
            .estado(UPDATED_ESTADO)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .pesoKg(UPDATED_PESO_KG)
            .valorDeclarado(UPDATED_VALOR_DECLARADO)
            .urlRastreo(UPDATED_URL_RASTREO)
            .observaciones(UPDATED_OBSERVACIONES)
            .fechaDespacho(UPDATED_FECHA_DESPACHO)
            .fechaEntregaEstimada(UPDATED_FECHA_ENTREGA_ESTIMADA)
            .fechaEntrega(UPDATED_FECHA_ENTREGA);

        restEnvioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvio))
            )
            .andExpect(status().isOk());

        // Validate the Envio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvioUpdatableFieldsEquals(partialUpdatedEnvio, getPersistedEnvio(partialUpdatedEnvio));
    }

    @Test
    void patchNonExistingEnvio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        envio.setId(UUID.randomUUID().toString());

        // Create the Envio
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, envioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(envioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEnvio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        envio.setId(UUID.randomUUID().toString());

        // Create the Envio
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(envioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEnvio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        envio.setId(UUID.randomUUID().toString());

        // Create the Envio
        EnvioDTO envioDTO = envioMapper.toDto(envio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(envioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Envio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEnvio() throws Exception {
        // Initialize the database
        insertedEnvio = envioRepository.save(envio);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the envio
        restEnvioMockMvc
            .perform(delete(ENTITY_API_URL_ID, envio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return envioRepository.count();
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

    protected Envio getPersistedEnvio(Envio envio) {
        return envioRepository.findById(envio.getId()).orElseThrow();
    }

    protected void assertPersistedEnvioToMatchAllProperties(Envio expectedEnvio) {
        assertEnvioAllPropertiesEquals(expectedEnvio, getPersistedEnvio(expectedEnvio));
    }

    protected void assertPersistedEnvioToMatchUpdatableProperties(Envio expectedEnvio) {
        assertEnvioAllUpdatablePropertiesEquals(expectedEnvio, getPersistedEnvio(expectedEnvio));
    }
}
