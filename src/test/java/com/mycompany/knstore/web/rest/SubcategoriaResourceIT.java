package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.SubcategoriaAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.repository.SubcategoriaRepository;
import com.mycompany.knstore.service.SubcategoriaService;
import com.mycompany.knstore.service.dto.SubcategoriaDTO;
import com.mycompany.knstore.service.mapper.SubcategoriaMapper;
import java.util.ArrayList;
import java.util.Base64;
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
 * Integration tests for the {@link SubcategoriaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SubcategoriaResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGEN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGEN = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGEN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGEN_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final String ENTITY_API_URL = "/api/subcategorias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    @Mock
    private SubcategoriaRepository subcategoriaRepositoryMock;

    @Autowired
    private SubcategoriaMapper subcategoriaMapper;

    @Mock
    private SubcategoriaService subcategoriaServiceMock;

    @Autowired
    private MockMvc restSubcategoriaMockMvc;

    private Subcategoria subcategoria;

    private Subcategoria insertedSubcategoria;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subcategoria createEntity() {
        Subcategoria subcategoria = new Subcategoria()
            .nombre(DEFAULT_NOMBRE)
            .slug(DEFAULT_SLUG)
            .descripcion(DEFAULT_DESCRIPCION)
            .imagen(DEFAULT_IMAGEN)
            .imagenContentType(DEFAULT_IMAGEN_CONTENT_TYPE)
            .activo(DEFAULT_ACTIVO);
        // Add required entity
        Categoria categoria;
        categoria = CategoriaResourceIT.createEntity();
        categoria.setId("fixed-id-for-tests");
        subcategoria.setCategoria(categoria);
        return subcategoria;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subcategoria createUpdatedEntity() {
        Subcategoria updatedSubcategoria = new Subcategoria()
            .nombre(UPDATED_NOMBRE)
            .slug(UPDATED_SLUG)
            .descripcion(UPDATED_DESCRIPCION)
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .activo(UPDATED_ACTIVO);
        // Add required entity
        Categoria categoria;
        categoria = CategoriaResourceIT.createUpdatedEntity();
        categoria.setId("fixed-id-for-tests");
        updatedSubcategoria.setCategoria(categoria);
        return updatedSubcategoria;
    }

    @BeforeEach
    void initTest() {
        subcategoria = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubcategoria != null) {
            subcategoriaRepository.delete(insertedSubcategoria);
            insertedSubcategoria = null;
        }
    }

    @Test
    void createSubcategoria() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Subcategoria
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);
        var returnedSubcategoriaDTO = om.readValue(
            restSubcategoriaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subcategoriaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubcategoriaDTO.class
        );

        // Validate the Subcategoria in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubcategoria = subcategoriaMapper.toEntity(returnedSubcategoriaDTO);
        assertSubcategoriaUpdatableFieldsEquals(returnedSubcategoria, getPersistedSubcategoria(returnedSubcategoria));

        insertedSubcategoria = returnedSubcategoria;
    }

    @Test
    void createSubcategoriaWithExistingId() throws Exception {
        // Create the Subcategoria with an existing ID
        subcategoria.setId("existing_id");
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubcategoriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subcategoriaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subcategoria.setNombre(null);

        // Create the Subcategoria, which fails.
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        restSubcategoriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subcategoriaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subcategoria.setSlug(null);

        // Create the Subcategoria, which fails.
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        restSubcategoriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subcategoriaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subcategoria.setActivo(null);

        // Create the Subcategoria, which fails.
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        restSubcategoriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subcategoriaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSubcategorias() throws Exception {
        // Initialize the database
        insertedSubcategoria = subcategoriaRepository.save(subcategoria);

        // Get all the subcategoriaList
        restSubcategoriaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subcategoria.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].imagenContentType").value(hasItem(DEFAULT_IMAGEN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imagen").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGEN))))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubcategoriasWithEagerRelationshipsIsEnabled() throws Exception {
        when(subcategoriaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSubcategoriaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(subcategoriaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubcategoriasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(subcategoriaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSubcategoriaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(subcategoriaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getSubcategoria() throws Exception {
        // Initialize the database
        insertedSubcategoria = subcategoriaRepository.save(subcategoria);

        // Get the subcategoria
        restSubcategoriaMockMvc
            .perform(get(ENTITY_API_URL_ID, subcategoria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subcategoria.getId()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.imagenContentType").value(DEFAULT_IMAGEN_CONTENT_TYPE))
            .andExpect(jsonPath("$.imagen").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGEN)))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO));
    }

    @Test
    void getNonExistingSubcategoria() throws Exception {
        // Get the subcategoria
        restSubcategoriaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingSubcategoria() throws Exception {
        // Initialize the database
        insertedSubcategoria = subcategoriaRepository.save(subcategoria);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subcategoria
        Subcategoria updatedSubcategoria = subcategoriaRepository.findById(subcategoria.getId()).orElseThrow();
        updatedSubcategoria
            .nombre(UPDATED_NOMBRE)
            .slug(UPDATED_SLUG)
            .descripcion(UPDATED_DESCRIPCION)
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .activo(UPDATED_ACTIVO);
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(updatedSubcategoria);

        restSubcategoriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subcategoriaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subcategoriaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubcategoriaToMatchAllProperties(updatedSubcategoria);
    }

    @Test
    void putNonExistingSubcategoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategoria.setId(UUID.randomUUID().toString());

        // Create the Subcategoria
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubcategoriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subcategoriaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subcategoriaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSubcategoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategoria.setId(UUID.randomUUID().toString());

        // Create the Subcategoria
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subcategoriaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSubcategoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategoria.setId(UUID.randomUUID().toString());

        // Create the Subcategoria
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoriaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subcategoriaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSubcategoriaWithPatch() throws Exception {
        // Initialize the database
        insertedSubcategoria = subcategoriaRepository.save(subcategoria);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subcategoria using partial update
        Subcategoria partialUpdatedSubcategoria = new Subcategoria();
        partialUpdatedSubcategoria.setId(subcategoria.getId());

        partialUpdatedSubcategoria.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION);

        restSubcategoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubcategoria.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubcategoria))
            )
            .andExpect(status().isOk());

        // Validate the Subcategoria in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubcategoriaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubcategoria, subcategoria),
            getPersistedSubcategoria(subcategoria)
        );
    }

    @Test
    void fullUpdateSubcategoriaWithPatch() throws Exception {
        // Initialize the database
        insertedSubcategoria = subcategoriaRepository.save(subcategoria);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subcategoria using partial update
        Subcategoria partialUpdatedSubcategoria = new Subcategoria();
        partialUpdatedSubcategoria.setId(subcategoria.getId());

        partialUpdatedSubcategoria
            .nombre(UPDATED_NOMBRE)
            .slug(UPDATED_SLUG)
            .descripcion(UPDATED_DESCRIPCION)
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .activo(UPDATED_ACTIVO);

        restSubcategoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubcategoria.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubcategoria))
            )
            .andExpect(status().isOk());

        // Validate the Subcategoria in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubcategoriaUpdatableFieldsEquals(partialUpdatedSubcategoria, getPersistedSubcategoria(partialUpdatedSubcategoria));
    }

    @Test
    void patchNonExistingSubcategoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategoria.setId(UUID.randomUUID().toString());

        // Create the Subcategoria
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubcategoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subcategoriaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subcategoriaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSubcategoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategoria.setId(UUID.randomUUID().toString());

        // Create the Subcategoria
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subcategoriaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSubcategoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategoria.setId(UUID.randomUUID().toString());

        // Create the Subcategoria
        SubcategoriaDTO subcategoriaDTO = subcategoriaMapper.toDto(subcategoria);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoriaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(subcategoriaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subcategoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSubcategoria() throws Exception {
        // Initialize the database
        insertedSubcategoria = subcategoriaRepository.save(subcategoria);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subcategoria
        restSubcategoriaMockMvc
            .perform(delete(ENTITY_API_URL_ID, subcategoria.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subcategoriaRepository.count();
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

    protected Subcategoria getPersistedSubcategoria(Subcategoria subcategoria) {
        return subcategoriaRepository.findById(subcategoria.getId()).orElseThrow();
    }

    protected void assertPersistedSubcategoriaToMatchAllProperties(Subcategoria expectedSubcategoria) {
        assertSubcategoriaAllPropertiesEquals(expectedSubcategoria, getPersistedSubcategoria(expectedSubcategoria));
    }

    protected void assertPersistedSubcategoriaToMatchUpdatableProperties(Subcategoria expectedSubcategoria) {
        assertSubcategoriaAllUpdatablePropertiesEquals(expectedSubcategoria, getPersistedSubcategoria(expectedSubcategoria));
    }
}
