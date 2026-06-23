package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.TipoDocumentoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.TipoDocumento;
import com.mycompany.knstore.domain.enumeration.EstadoTipoDocumento;
import com.mycompany.knstore.repository.TipoDocumentoRepository;
import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import com.mycompany.knstore.service.mapper.TipoDocumentoMapper;
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
 * Integration tests for the {@link TipoDocumentoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TipoDocumentoResourceIT {

    private static final String DEFAULT_SIGLA = "AAAAAAAAAA";
    private static final String UPDATED_SIGLA = "BBBBBBBBBB";

    private static final String DEFAULT_NOMBRE_TIPO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_TIPO = "BBBBBBBBBB";

    private static final EstadoTipoDocumento DEFAULT_ESTADO = EstadoTipoDocumento.ACTIVO;
    private static final EstadoTipoDocumento UPDATED_ESTADO = EstadoTipoDocumento.INACTIVO;

    private static final String ENTITY_API_URL = "/api/tipo-documentos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Autowired
    private TipoDocumentoMapper tipoDocumentoMapper;

    @Autowired
    private MockMvc restTipoDocumentoMockMvc;

    private TipoDocumento tipoDocumento;

    private TipoDocumento insertedTipoDocumento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoDocumento createEntity() {
        return new TipoDocumento().sigla(DEFAULT_SIGLA).nombreTipo(DEFAULT_NOMBRE_TIPO).estado(DEFAULT_ESTADO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoDocumento createUpdatedEntity() {
        return new TipoDocumento().sigla(UPDATED_SIGLA).nombreTipo(UPDATED_NOMBRE_TIPO).estado(UPDATED_ESTADO);
    }

    @BeforeEach
    void initTest() {
        tipoDocumento = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTipoDocumento != null) {
            tipoDocumentoRepository.delete(insertedTipoDocumento);
            insertedTipoDocumento = null;
        }
    }

    @Test
    void createTipoDocumento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TipoDocumento
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);
        var returnedTipoDocumentoDTO = om.readValue(
            restTipoDocumentoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDocumentoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TipoDocumentoDTO.class
        );

        // Validate the TipoDocumento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTipoDocumento = tipoDocumentoMapper.toEntity(returnedTipoDocumentoDTO);
        assertTipoDocumentoUpdatableFieldsEquals(returnedTipoDocumento, getPersistedTipoDocumento(returnedTipoDocumento));

        insertedTipoDocumento = returnedTipoDocumento;
    }

    @Test
    void createTipoDocumentoWithExistingId() throws Exception {
        // Create the TipoDocumento with an existing ID
        tipoDocumento.setId("existing_id");
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTipoDocumentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDocumentoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkSiglaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tipoDocumento.setSigla(null);

        // Create the TipoDocumento, which fails.
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        restTipoDocumentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDocumentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNombreTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tipoDocumento.setNombreTipo(null);

        // Create the TipoDocumento, which fails.
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        restTipoDocumentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDocumentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tipoDocumento.setEstado(null);

        // Create the TipoDocumento, which fails.
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        restTipoDocumentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDocumentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTipoDocumentos() throws Exception {
        // Initialize the database
        insertedTipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        // Get all the tipoDocumentoList
        restTipoDocumentoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipoDocumento.getId())))
            .andExpect(jsonPath("$.[*].sigla").value(hasItem(DEFAULT_SIGLA)))
            .andExpect(jsonPath("$.[*].nombreTipo").value(hasItem(DEFAULT_NOMBRE_TIPO)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())));
    }

    @Test
    void getTipoDocumento() throws Exception {
        // Initialize the database
        insertedTipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        // Get the tipoDocumento
        restTipoDocumentoMockMvc
            .perform(get(ENTITY_API_URL_ID, tipoDocumento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tipoDocumento.getId()))
            .andExpect(jsonPath("$.sigla").value(DEFAULT_SIGLA))
            .andExpect(jsonPath("$.nombreTipo").value(DEFAULT_NOMBRE_TIPO))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()));
    }

    @Test
    void getNonExistingTipoDocumento() throws Exception {
        // Get the tipoDocumento
        restTipoDocumentoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingTipoDocumento() throws Exception {
        // Initialize the database
        insertedTipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tipoDocumento
        TipoDocumento updatedTipoDocumento = tipoDocumentoRepository.findById(tipoDocumento.getId()).orElseThrow();
        updatedTipoDocumento.sigla(UPDATED_SIGLA).nombreTipo(UPDATED_NOMBRE_TIPO).estado(UPDATED_ESTADO);
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(updatedTipoDocumento);

        restTipoDocumentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tipoDocumentoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tipoDocumentoDTO))
            )
            .andExpect(status().isOk());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTipoDocumentoToMatchAllProperties(updatedTipoDocumento);
    }

    @Test
    void putNonExistingTipoDocumento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tipoDocumento.setId(UUID.randomUUID().toString());

        // Create the TipoDocumento
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipoDocumentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tipoDocumentoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tipoDocumentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTipoDocumento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tipoDocumento.setId(UUID.randomUUID().toString());

        // Create the TipoDocumento
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoDocumentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tipoDocumentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTipoDocumento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tipoDocumento.setId(UUID.randomUUID().toString());

        // Create the TipoDocumento
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoDocumentoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDocumentoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTipoDocumentoWithPatch() throws Exception {
        // Initialize the database
        insertedTipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tipoDocumento using partial update
        TipoDocumento partialUpdatedTipoDocumento = new TipoDocumento();
        partialUpdatedTipoDocumento.setId(tipoDocumento.getId());

        partialUpdatedTipoDocumento.sigla(UPDATED_SIGLA);

        restTipoDocumentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTipoDocumento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTipoDocumento))
            )
            .andExpect(status().isOk());

        // Validate the TipoDocumento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTipoDocumentoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTipoDocumento, tipoDocumento),
            getPersistedTipoDocumento(tipoDocumento)
        );
    }

    @Test
    void fullUpdateTipoDocumentoWithPatch() throws Exception {
        // Initialize the database
        insertedTipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tipoDocumento using partial update
        TipoDocumento partialUpdatedTipoDocumento = new TipoDocumento();
        partialUpdatedTipoDocumento.setId(tipoDocumento.getId());

        partialUpdatedTipoDocumento.sigla(UPDATED_SIGLA).nombreTipo(UPDATED_NOMBRE_TIPO).estado(UPDATED_ESTADO);

        restTipoDocumentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTipoDocumento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTipoDocumento))
            )
            .andExpect(status().isOk());

        // Validate the TipoDocumento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTipoDocumentoUpdatableFieldsEquals(partialUpdatedTipoDocumento, getPersistedTipoDocumento(partialUpdatedTipoDocumento));
    }

    @Test
    void patchNonExistingTipoDocumento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tipoDocumento.setId(UUID.randomUUID().toString());

        // Create the TipoDocumento
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipoDocumentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tipoDocumentoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tipoDocumentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTipoDocumento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tipoDocumento.setId(UUID.randomUUID().toString());

        // Create the TipoDocumento
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoDocumentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tipoDocumentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTipoDocumento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tipoDocumento.setId(UUID.randomUUID().toString());

        // Create the TipoDocumento
        TipoDocumentoDTO tipoDocumentoDTO = tipoDocumentoMapper.toDto(tipoDocumento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoDocumentoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tipoDocumentoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TipoDocumento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTipoDocumento() throws Exception {
        // Initialize the database
        insertedTipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tipoDocumento
        restTipoDocumentoMockMvc
            .perform(delete(ENTITY_API_URL_ID, tipoDocumento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tipoDocumentoRepository.count();
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

    protected TipoDocumento getPersistedTipoDocumento(TipoDocumento tipoDocumento) {
        return tipoDocumentoRepository.findById(tipoDocumento.getId()).orElseThrow();
    }

    protected void assertPersistedTipoDocumentoToMatchAllProperties(TipoDocumento expectedTipoDocumento) {
        assertTipoDocumentoAllPropertiesEquals(expectedTipoDocumento, getPersistedTipoDocumento(expectedTipoDocumento));
    }

    protected void assertPersistedTipoDocumentoToMatchUpdatableProperties(TipoDocumento expectedTipoDocumento) {
        assertTipoDocumentoAllUpdatablePropertiesEquals(expectedTipoDocumento, getPersistedTipoDocumento(expectedTipoDocumento));
    }
}
