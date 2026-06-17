package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.EtiquetaProductoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.EtiquetaProducto;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.EtiquetaProductoRepository;
import com.mycompany.knstore.service.EtiquetaProductoService;
import com.mycompany.knstore.service.dto.EtiquetaProductoDTO;
import com.mycompany.knstore.service.mapper.EtiquetaProductoMapper;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link EtiquetaProductoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EtiquetaProductoResourceIT {

    private static final String DEFAULT_ETIQUETA = "AAAAAAAAAA";
    private static final String UPDATED_ETIQUETA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/etiqueta-productos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EtiquetaProductoRepository etiquetaProductoRepository;

    @Mock
    private EtiquetaProductoRepository etiquetaProductoRepositoryMock;

    @Autowired
    private EtiquetaProductoMapper etiquetaProductoMapper;

    @Mock
    private EtiquetaProductoService etiquetaProductoServiceMock;

    @Autowired
    private MockMvc restEtiquetaProductoMockMvc;

    private EtiquetaProducto etiquetaProducto;

    private EtiquetaProducto insertedEtiquetaProducto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtiquetaProducto createEntity() {
        EtiquetaProducto etiquetaProducto = new EtiquetaProducto().etiqueta(DEFAULT_ETIQUETA);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createEntity();
        producto.setId("fixed-id-for-tests");
        etiquetaProducto.setProducto(producto);
        return etiquetaProducto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtiquetaProducto createUpdatedEntity() {
        EtiquetaProducto updatedEtiquetaProducto = new EtiquetaProducto().etiqueta(UPDATED_ETIQUETA);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createUpdatedEntity();
        producto.setId("fixed-id-for-tests");
        updatedEtiquetaProducto.setProducto(producto);
        return updatedEtiquetaProducto;
    }

    @BeforeEach
    void initTest() {
        etiquetaProducto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEtiquetaProducto != null) {
            etiquetaProductoRepository.delete(insertedEtiquetaProducto);
            insertedEtiquetaProducto = null;
        }
    }

    @Test
    void createEtiquetaProducto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EtiquetaProducto
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);
        var returnedEtiquetaProductoDTO = om.readValue(
            restEtiquetaProductoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetaProductoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EtiquetaProductoDTO.class
        );

        // Validate the EtiquetaProducto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEtiquetaProducto = etiquetaProductoMapper.toEntity(returnedEtiquetaProductoDTO);
        assertEtiquetaProductoUpdatableFieldsEquals(returnedEtiquetaProducto, getPersistedEtiquetaProducto(returnedEtiquetaProducto));

        insertedEtiquetaProducto = returnedEtiquetaProducto;
    }

    @Test
    void createEtiquetaProductoWithExistingId() throws Exception {
        // Create the EtiquetaProducto with an existing ID
        etiquetaProducto.setId("existing_id");
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEtiquetaProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetaProductoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEtiquetaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etiquetaProducto.setEtiqueta(null);

        // Create the EtiquetaProducto, which fails.
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        restEtiquetaProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetaProductoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEtiquetaProductos() throws Exception {
        // Initialize the database
        insertedEtiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);

        // Get all the etiquetaProductoList
        restEtiquetaProductoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etiquetaProducto.getId())))
            .andExpect(jsonPath("$.[*].etiqueta").value(hasItem(DEFAULT_ETIQUETA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEtiquetaProductosWithEagerRelationshipsIsEnabled() throws Exception {
        when(etiquetaProductoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEtiquetaProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(etiquetaProductoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEtiquetaProductosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(etiquetaProductoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEtiquetaProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(etiquetaProductoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getEtiquetaProducto() throws Exception {
        // Initialize the database
        insertedEtiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);

        // Get the etiquetaProducto
        restEtiquetaProductoMockMvc
            .perform(get(ENTITY_API_URL_ID, etiquetaProducto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(etiquetaProducto.getId()))
            .andExpect(jsonPath("$.etiqueta").value(DEFAULT_ETIQUETA));
    }

    @Test
    void getNonExistingEtiquetaProducto() throws Exception {
        // Get the etiquetaProducto
        restEtiquetaProductoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingEtiquetaProducto() throws Exception {
        // Initialize the database
        insertedEtiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiquetaProducto
        EtiquetaProducto updatedEtiquetaProducto = etiquetaProductoRepository.findById(etiquetaProducto.getId()).orElseThrow();
        updatedEtiquetaProducto.etiqueta(UPDATED_ETIQUETA);
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(updatedEtiquetaProducto);

        restEtiquetaProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etiquetaProductoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etiquetaProductoDTO))
            )
            .andExpect(status().isOk());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEtiquetaProductoToMatchAllProperties(updatedEtiquetaProducto);
    }

    @Test
    void putNonExistingEtiquetaProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetaProducto.setId(UUID.randomUUID().toString());

        // Create the EtiquetaProducto
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtiquetaProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etiquetaProductoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etiquetaProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEtiquetaProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetaProducto.setId(UUID.randomUUID().toString());

        // Create the EtiquetaProducto
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetaProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etiquetaProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEtiquetaProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetaProducto.setId(UUID.randomUUID().toString());

        // Create the EtiquetaProducto
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetaProductoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetaProductoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEtiquetaProductoWithPatch() throws Exception {
        // Initialize the database
        insertedEtiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiquetaProducto using partial update
        EtiquetaProducto partialUpdatedEtiquetaProducto = new EtiquetaProducto();
        partialUpdatedEtiquetaProducto.setId(etiquetaProducto.getId());

        restEtiquetaProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtiquetaProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtiquetaProducto))
            )
            .andExpect(status().isOk());

        // Validate the EtiquetaProducto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtiquetaProductoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEtiquetaProducto, etiquetaProducto),
            getPersistedEtiquetaProducto(etiquetaProducto)
        );
    }

    @Test
    void fullUpdateEtiquetaProductoWithPatch() throws Exception {
        // Initialize the database
        insertedEtiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiquetaProducto using partial update
        EtiquetaProducto partialUpdatedEtiquetaProducto = new EtiquetaProducto();
        partialUpdatedEtiquetaProducto.setId(etiquetaProducto.getId());

        partialUpdatedEtiquetaProducto.etiqueta(UPDATED_ETIQUETA);

        restEtiquetaProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtiquetaProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtiquetaProducto))
            )
            .andExpect(status().isOk());

        // Validate the EtiquetaProducto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtiquetaProductoUpdatableFieldsEquals(
            partialUpdatedEtiquetaProducto,
            getPersistedEtiquetaProducto(partialUpdatedEtiquetaProducto)
        );
    }

    @Test
    void patchNonExistingEtiquetaProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetaProducto.setId(UUID.randomUUID().toString());

        // Create the EtiquetaProducto
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtiquetaProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, etiquetaProductoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etiquetaProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEtiquetaProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetaProducto.setId(UUID.randomUUID().toString());

        // Create the EtiquetaProducto
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetaProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etiquetaProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEtiquetaProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetaProducto.setId(UUID.randomUUID().toString());

        // Create the EtiquetaProducto
        EtiquetaProductoDTO etiquetaProductoDTO = etiquetaProductoMapper.toDto(etiquetaProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetaProductoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(etiquetaProductoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtiquetaProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEtiquetaProducto() throws Exception {
        // Initialize the database
        insertedEtiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the etiquetaProducto
        restEtiquetaProductoMockMvc
            .perform(delete(ENTITY_API_URL_ID, etiquetaProducto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return etiquetaProductoRepository.count();
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

    protected EtiquetaProducto getPersistedEtiquetaProducto(EtiquetaProducto etiquetaProducto) {
        return etiquetaProductoRepository.findById(etiquetaProducto.getId()).orElseThrow();
    }

    protected void assertPersistedEtiquetaProductoToMatchAllProperties(EtiquetaProducto expectedEtiquetaProducto) {
        assertEtiquetaProductoAllPropertiesEquals(expectedEtiquetaProducto, getPersistedEtiquetaProducto(expectedEtiquetaProducto));
    }

    protected void assertPersistedEtiquetaProductoToMatchUpdatableProperties(EtiquetaProducto expectedEtiquetaProducto) {
        assertEtiquetaProductoAllUpdatablePropertiesEquals(
            expectedEtiquetaProducto,
            getPersistedEtiquetaProducto(expectedEtiquetaProducto)
        );
    }
}
