package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.ProductoImagenAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.ProductoImagen;
import com.mycompany.knstore.repository.ProductoImagenRepository;
import com.mycompany.knstore.service.dto.ProductoImagenDTO;
import com.mycompany.knstore.service.mapper.ProductoImagenMapper;
import java.util.Base64;
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
 * Integration tests for the {@link ProductoImagenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductoImagenResourceIT {

    private static final byte[] DEFAULT_IMAGEN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGEN = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGEN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGEN_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_IMAGEN_ALT = "AAAAAAAAAA";
    private static final String UPDATED_IMAGEN_ALT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ES_PRINCIPAL = false;
    private static final Boolean UPDATED_ES_PRINCIPAL = true;

    private static final String ENTITY_API_URL = "/api/producto-imagens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductoImagenRepository productoImagenRepository;

    @Autowired
    private ProductoImagenMapper productoImagenMapper;

    @Autowired
    private MockMvc restProductoImagenMockMvc;

    private ProductoImagen productoImagen;

    private ProductoImagen insertedProductoImagen;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductoImagen createEntity() {
        return new ProductoImagen()
            .imagen(DEFAULT_IMAGEN)
            .imagenContentType(DEFAULT_IMAGEN_CONTENT_TYPE)
            .imagenAlt(DEFAULT_IMAGEN_ALT)
            .esPrincipal(DEFAULT_ES_PRINCIPAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductoImagen createUpdatedEntity() {
        return new ProductoImagen()
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .imagenAlt(UPDATED_IMAGEN_ALT)
            .esPrincipal(UPDATED_ES_PRINCIPAL);
    }

    @BeforeEach
    void initTest() {
        productoImagen = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductoImagen != null) {
            productoImagenRepository.delete(insertedProductoImagen);
            insertedProductoImagen = null;
        }
    }

    @Test
    void createProductoImagen() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductoImagen
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);
        var returnedProductoImagenDTO = om.readValue(
            restProductoImagenMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoImagenDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductoImagenDTO.class
        );

        // Validate the ProductoImagen in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductoImagen = productoImagenMapper.toEntity(returnedProductoImagenDTO);
        assertProductoImagenUpdatableFieldsEquals(returnedProductoImagen, getPersistedProductoImagen(returnedProductoImagen));

        insertedProductoImagen = returnedProductoImagen;
    }

    @Test
    void createProductoImagenWithExistingId() throws Exception {
        // Create the ProductoImagen with an existing ID
        productoImagen.setId("existing_id");
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductoImagenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoImagenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEsPrincipalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productoImagen.setEsPrincipal(null);

        // Create the ProductoImagen, which fails.
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        restProductoImagenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoImagenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProductoImagens() throws Exception {
        // Initialize the database
        insertedProductoImagen = productoImagenRepository.save(productoImagen);

        // Get all the productoImagenList
        restProductoImagenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productoImagen.getId())))
            .andExpect(jsonPath("$.[*].imagenContentType").value(hasItem(DEFAULT_IMAGEN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imagen").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGEN))))
            .andExpect(jsonPath("$.[*].imagenAlt").value(hasItem(DEFAULT_IMAGEN_ALT)))
            .andExpect(jsonPath("$.[*].esPrincipal").value(hasItem(DEFAULT_ES_PRINCIPAL)));
    }

    @Test
    void getProductoImagen() throws Exception {
        // Initialize the database
        insertedProductoImagen = productoImagenRepository.save(productoImagen);

        // Get the productoImagen
        restProductoImagenMockMvc
            .perform(get(ENTITY_API_URL_ID, productoImagen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productoImagen.getId()))
            .andExpect(jsonPath("$.imagenContentType").value(DEFAULT_IMAGEN_CONTENT_TYPE))
            .andExpect(jsonPath("$.imagen").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGEN)))
            .andExpect(jsonPath("$.imagenAlt").value(DEFAULT_IMAGEN_ALT))
            .andExpect(jsonPath("$.esPrincipal").value(DEFAULT_ES_PRINCIPAL));
    }

    @Test
    void getNonExistingProductoImagen() throws Exception {
        // Get the productoImagen
        restProductoImagenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingProductoImagen() throws Exception {
        // Initialize the database
        insertedProductoImagen = productoImagenRepository.save(productoImagen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoImagen
        ProductoImagen updatedProductoImagen = productoImagenRepository.findById(productoImagen.getId()).orElseThrow();
        updatedProductoImagen
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .imagenAlt(UPDATED_IMAGEN_ALT)
            .esPrincipal(UPDATED_ES_PRINCIPAL);
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(updatedProductoImagen);

        restProductoImagenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoImagenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoImagenDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductoImagenToMatchAllProperties(updatedProductoImagen);
    }

    @Test
    void putNonExistingProductoImagen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoImagen.setId(UUID.randomUUID().toString());

        // Create the ProductoImagen
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoImagenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoImagenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoImagenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProductoImagen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoImagen.setId(UUID.randomUUID().toString());

        // Create the ProductoImagen
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoImagenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoImagenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProductoImagen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoImagen.setId(UUID.randomUUID().toString());

        // Create the ProductoImagen
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoImagenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoImagenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductoImagenWithPatch() throws Exception {
        // Initialize the database
        insertedProductoImagen = productoImagenRepository.save(productoImagen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoImagen using partial update
        ProductoImagen partialUpdatedProductoImagen = new ProductoImagen();
        partialUpdatedProductoImagen.setId(productoImagen.getId());

        partialUpdatedProductoImagen.imagenAlt(UPDATED_IMAGEN_ALT).esPrincipal(UPDATED_ES_PRINCIPAL);

        restProductoImagenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductoImagen.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductoImagen))
            )
            .andExpect(status().isOk());

        // Validate the ProductoImagen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoImagenUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductoImagen, productoImagen),
            getPersistedProductoImagen(productoImagen)
        );
    }

    @Test
    void fullUpdateProductoImagenWithPatch() throws Exception {
        // Initialize the database
        insertedProductoImagen = productoImagenRepository.save(productoImagen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoImagen using partial update
        ProductoImagen partialUpdatedProductoImagen = new ProductoImagen();
        partialUpdatedProductoImagen.setId(productoImagen.getId());

        partialUpdatedProductoImagen
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .imagenAlt(UPDATED_IMAGEN_ALT)
            .esPrincipal(UPDATED_ES_PRINCIPAL);

        restProductoImagenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductoImagen.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductoImagen))
            )
            .andExpect(status().isOk());

        // Validate the ProductoImagen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoImagenUpdatableFieldsEquals(partialUpdatedProductoImagen, getPersistedProductoImagen(partialUpdatedProductoImagen));
    }

    @Test
    void patchNonExistingProductoImagen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoImagen.setId(UUID.randomUUID().toString());

        // Create the ProductoImagen
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoImagenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productoImagenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoImagenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProductoImagen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoImagen.setId(UUID.randomUUID().toString());

        // Create the ProductoImagen
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoImagenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoImagenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProductoImagen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoImagen.setId(UUID.randomUUID().toString());

        // Create the ProductoImagen
        ProductoImagenDTO productoImagenDTO = productoImagenMapper.toDto(productoImagen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoImagenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productoImagenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductoImagen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProductoImagen() throws Exception {
        // Initialize the database
        insertedProductoImagen = productoImagenRepository.save(productoImagen);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productoImagen
        restProductoImagenMockMvc
            .perform(delete(ENTITY_API_URL_ID, productoImagen.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productoImagenRepository.count();
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

    protected ProductoImagen getPersistedProductoImagen(ProductoImagen productoImagen) {
        return productoImagenRepository.findById(productoImagen.getId()).orElseThrow();
    }

    protected void assertPersistedProductoImagenToMatchAllProperties(ProductoImagen expectedProductoImagen) {
        assertProductoImagenAllPropertiesEquals(expectedProductoImagen, getPersistedProductoImagen(expectedProductoImagen));
    }

    protected void assertPersistedProductoImagenToMatchUpdatableProperties(ProductoImagen expectedProductoImagen) {
        assertProductoImagenAllUpdatablePropertiesEquals(expectedProductoImagen, getPersistedProductoImagen(expectedProductoImagen));
    }
}
