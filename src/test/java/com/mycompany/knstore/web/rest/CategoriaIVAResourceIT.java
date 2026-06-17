package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.CategoriaIVAAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.enumeration.EstadoIVA;
import com.mycompany.knstore.repository.CategoriaIVARepository;
import com.mycompany.knstore.service.dto.CategoriaIVADTO;
import com.mycompany.knstore.service.mapper.CategoriaIVAMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CategoriaIVAResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategoriaIVAResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PORCENTAJE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PORCENTAJE = new BigDecimal(1);

    private static final EstadoIVA DEFAULT_ESTADO = EstadoIVA.ACTIVO;
    private static final EstadoIVA UPDATED_ESTADO = EstadoIVA.INACTIVO;

    private static final String ENTITY_API_URL = "/api/categoria-ivas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CategoriaIVARepository categoriaIVARepository;

    @Autowired
    private CategoriaIVAMapper categoriaIVAMapper;

    @Autowired
    private MockMvc restCategoriaIVAMockMvc;

    private CategoriaIVA categoriaIVA;

    private CategoriaIVA insertedCategoriaIVA;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CategoriaIVA createEntity() {
        return new CategoriaIVA().nombre(DEFAULT_NOMBRE).porcentaje(DEFAULT_PORCENTAJE).estado(DEFAULT_ESTADO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CategoriaIVA createUpdatedEntity() {
        return new CategoriaIVA().nombre(UPDATED_NOMBRE).porcentaje(UPDATED_PORCENTAJE).estado(UPDATED_ESTADO);
    }

    @BeforeEach
    void initTest() {
        categoriaIVA = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCategoriaIVA != null) {
            categoriaIVARepository.delete(insertedCategoriaIVA);
            insertedCategoriaIVA = null;
        }
    }

    @Test
    void createCategoriaIVA() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CategoriaIVA
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);
        var returnedCategoriaIVADTO = om.readValue(
            restCategoriaIVAMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoriaIVADTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CategoriaIVADTO.class
        );

        // Validate the CategoriaIVA in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCategoriaIVA = categoriaIVAMapper.toEntity(returnedCategoriaIVADTO);
        assertCategoriaIVAUpdatableFieldsEquals(returnedCategoriaIVA, getPersistedCategoriaIVA(returnedCategoriaIVA));

        insertedCategoriaIVA = returnedCategoriaIVA;
    }

    @Test
    void createCategoriaIVAWithExistingId() throws Exception {
        // Create the CategoriaIVA with an existing ID
        categoriaIVA.setId("existing_id");
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoriaIVAMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoriaIVADTO)))
            .andExpect(status().isBadRequest());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        categoriaIVA.setNombre(null);

        // Create the CategoriaIVA, which fails.
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        restCategoriaIVAMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoriaIVADTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPorcentajeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        categoriaIVA.setPorcentaje(null);

        // Create the CategoriaIVA, which fails.
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        restCategoriaIVAMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoriaIVADTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        categoriaIVA.setEstado(null);

        // Create the CategoriaIVA, which fails.
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        restCategoriaIVAMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoriaIVADTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCategoriaIVAS() throws Exception {
        // Initialize the database
        insertedCategoriaIVA = categoriaIVARepository.save(categoriaIVA);

        // Get all the categoriaIVAList
        restCategoriaIVAMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categoriaIVA.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].porcentaje").value(hasItem(sameNumber(DEFAULT_PORCENTAJE))))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())));
    }

    @Test
    void getCategoriaIVA() throws Exception {
        // Initialize the database
        insertedCategoriaIVA = categoriaIVARepository.save(categoriaIVA);

        // Get the categoriaIVA
        restCategoriaIVAMockMvc
            .perform(get(ENTITY_API_URL_ID, categoriaIVA.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(categoriaIVA.getId()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.porcentaje").value(sameNumber(DEFAULT_PORCENTAJE)))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()));
    }

    @Test
    void getNonExistingCategoriaIVA() throws Exception {
        // Get the categoriaIVA
        restCategoriaIVAMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingCategoriaIVA() throws Exception {
        // Initialize the database
        insertedCategoriaIVA = categoriaIVARepository.save(categoriaIVA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categoriaIVA
        CategoriaIVA updatedCategoriaIVA = categoriaIVARepository.findById(categoriaIVA.getId()).orElseThrow();
        updatedCategoriaIVA.nombre(UPDATED_NOMBRE).porcentaje(UPDATED_PORCENTAJE).estado(UPDATED_ESTADO);
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(updatedCategoriaIVA);

        restCategoriaIVAMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoriaIVADTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categoriaIVADTO))
            )
            .andExpect(status().isOk());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCategoriaIVAToMatchAllProperties(updatedCategoriaIVA);
    }

    @Test
    void putNonExistingCategoriaIVA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoriaIVA.setId(UUID.randomUUID().toString());

        // Create the CategoriaIVA
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoriaIVAMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoriaIVADTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categoriaIVADTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCategoriaIVA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoriaIVA.setId(UUID.randomUUID().toString());

        // Create the CategoriaIVA
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriaIVAMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categoriaIVADTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCategoriaIVA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoriaIVA.setId(UUID.randomUUID().toString());

        // Create the CategoriaIVA
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriaIVAMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoriaIVADTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCategoriaIVAWithPatch() throws Exception {
        // Initialize the database
        insertedCategoriaIVA = categoriaIVARepository.save(categoriaIVA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categoriaIVA using partial update
        CategoriaIVA partialUpdatedCategoriaIVA = new CategoriaIVA();
        partialUpdatedCategoriaIVA.setId(categoriaIVA.getId());

        partialUpdatedCategoriaIVA.porcentaje(UPDATED_PORCENTAJE);

        restCategoriaIVAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategoriaIVA.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCategoriaIVA))
            )
            .andExpect(status().isOk());

        // Validate the CategoriaIVA in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategoriaIVAUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCategoriaIVA, categoriaIVA),
            getPersistedCategoriaIVA(categoriaIVA)
        );
    }

    @Test
    void fullUpdateCategoriaIVAWithPatch() throws Exception {
        // Initialize the database
        insertedCategoriaIVA = categoriaIVARepository.save(categoriaIVA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categoriaIVA using partial update
        CategoriaIVA partialUpdatedCategoriaIVA = new CategoriaIVA();
        partialUpdatedCategoriaIVA.setId(categoriaIVA.getId());

        partialUpdatedCategoriaIVA.nombre(UPDATED_NOMBRE).porcentaje(UPDATED_PORCENTAJE).estado(UPDATED_ESTADO);

        restCategoriaIVAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategoriaIVA.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCategoriaIVA))
            )
            .andExpect(status().isOk());

        // Validate the CategoriaIVA in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategoriaIVAUpdatableFieldsEquals(partialUpdatedCategoriaIVA, getPersistedCategoriaIVA(partialUpdatedCategoriaIVA));
    }

    @Test
    void patchNonExistingCategoriaIVA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoriaIVA.setId(UUID.randomUUID().toString());

        // Create the CategoriaIVA
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoriaIVAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, categoriaIVADTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(categoriaIVADTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCategoriaIVA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoriaIVA.setId(UUID.randomUUID().toString());

        // Create the CategoriaIVA
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriaIVAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(categoriaIVADTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCategoriaIVA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoriaIVA.setId(UUID.randomUUID().toString());

        // Create the CategoriaIVA
        CategoriaIVADTO categoriaIVADTO = categoriaIVAMapper.toDto(categoriaIVA);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoriaIVAMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(categoriaIVADTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CategoriaIVA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCategoriaIVA() throws Exception {
        // Initialize the database
        insertedCategoriaIVA = categoriaIVARepository.save(categoriaIVA);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the categoriaIVA
        restCategoriaIVAMockMvc
            .perform(delete(ENTITY_API_URL_ID, categoriaIVA.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return categoriaIVARepository.count();
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

    protected CategoriaIVA getPersistedCategoriaIVA(CategoriaIVA categoriaIVA) {
        return categoriaIVARepository.findById(categoriaIVA.getId()).orElseThrow();
    }

    protected void assertPersistedCategoriaIVAToMatchAllProperties(CategoriaIVA expectedCategoriaIVA) {
        assertCategoriaIVAAllPropertiesEquals(expectedCategoriaIVA, getPersistedCategoriaIVA(expectedCategoriaIVA));
    }

    protected void assertPersistedCategoriaIVAToMatchUpdatableProperties(CategoriaIVA expectedCategoriaIVA) {
        assertCategoriaIVAAllUpdatablePropertiesEquals(expectedCategoriaIVA, getPersistedCategoriaIVA(expectedCategoriaIVA));
    }
}
