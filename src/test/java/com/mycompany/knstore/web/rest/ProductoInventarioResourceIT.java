package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.ProductoInventarioAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.enumeration.UbicacionBodega;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.service.dto.ProductoInventarioDTO;
import com.mycompany.knstore.service.mapper.ProductoInventarioMapper;
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
 * Integration tests for the {@link ProductoInventarioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductoInventarioResourceIT {

    private static final Integer DEFAULT_STOCK = 0;
    private static final Integer UPDATED_STOCK = 1;

    private static final Integer DEFAULT_STOCK_MINIMO = 0;
    private static final Integer UPDATED_STOCK_MINIMO = 1;

    private static final UbicacionBodega DEFAULT_UBICACION_BODEGA = UbicacionBodega.BODEGA_PRINCIPAL;
    private static final UbicacionBodega UPDATED_UBICACION_BODEGA = UbicacionBodega.BODEGA_SECUNDARIA;

    private static final Integer DEFAULT_GARANTIA_MESES = 0;
    private static final Integer UPDATED_GARANTIA_MESES = 1;

    private static final String ENTITY_API_URL = "/api/producto-inventarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductoInventarioRepository productoInventarioRepository;

    @Autowired
    private ProductoInventarioMapper productoInventarioMapper;

    @Autowired
    private MockMvc restProductoInventarioMockMvc;

    private ProductoInventario productoInventario;

    private ProductoInventario insertedProductoInventario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductoInventario createEntity() {
        return new ProductoInventario()
            .stock(DEFAULT_STOCK)
            .stockMinimo(DEFAULT_STOCK_MINIMO)
            .ubicacionBodega(DEFAULT_UBICACION_BODEGA)
            .garantiaMeses(DEFAULT_GARANTIA_MESES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductoInventario createUpdatedEntity() {
        return new ProductoInventario()
            .stock(UPDATED_STOCK)
            .stockMinimo(UPDATED_STOCK_MINIMO)
            .ubicacionBodega(UPDATED_UBICACION_BODEGA)
            .garantiaMeses(UPDATED_GARANTIA_MESES);
    }

    @BeforeEach
    void initTest() {
        productoInventario = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductoInventario != null) {
            productoInventarioRepository.delete(insertedProductoInventario);
            insertedProductoInventario = null;
        }
    }

    @Test
    void createProductoInventario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductoInventario
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);
        var returnedProductoInventarioDTO = om.readValue(
            restProductoInventarioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoInventarioDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductoInventarioDTO.class
        );

        // Validate the ProductoInventario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductoInventario = productoInventarioMapper.toEntity(returnedProductoInventarioDTO);
        assertProductoInventarioUpdatableFieldsEquals(
            returnedProductoInventario,
            getPersistedProductoInventario(returnedProductoInventario)
        );

        insertedProductoInventario = returnedProductoInventario;
    }

    @Test
    void createProductoInventarioWithExistingId() throws Exception {
        // Create the ProductoInventario with an existing ID
        productoInventario.setId("existing_id");
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductoInventarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoInventarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStockIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productoInventario.setStock(null);

        // Create the ProductoInventario, which fails.
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        restProductoInventarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoInventarioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProductoInventarios() throws Exception {
        // Initialize the database
        insertedProductoInventario = productoInventarioRepository.save(productoInventario);

        // Get all the productoInventarioList
        restProductoInventarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productoInventario.getId())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK)))
            .andExpect(jsonPath("$.[*].stockMinimo").value(hasItem(DEFAULT_STOCK_MINIMO)))
            .andExpect(jsonPath("$.[*].ubicacionBodega").value(hasItem(DEFAULT_UBICACION_BODEGA.toString())))
            .andExpect(jsonPath("$.[*].garantiaMeses").value(hasItem(DEFAULT_GARANTIA_MESES)));
    }

    @Test
    void getProductoInventario() throws Exception {
        // Initialize the database
        insertedProductoInventario = productoInventarioRepository.save(productoInventario);

        // Get the productoInventario
        restProductoInventarioMockMvc
            .perform(get(ENTITY_API_URL_ID, productoInventario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productoInventario.getId()))
            .andExpect(jsonPath("$.stock").value(DEFAULT_STOCK))
            .andExpect(jsonPath("$.stockMinimo").value(DEFAULT_STOCK_MINIMO))
            .andExpect(jsonPath("$.ubicacionBodega").value(DEFAULT_UBICACION_BODEGA.toString()))
            .andExpect(jsonPath("$.garantiaMeses").value(DEFAULT_GARANTIA_MESES));
    }

    @Test
    void getNonExistingProductoInventario() throws Exception {
        // Get the productoInventario
        restProductoInventarioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingProductoInventario() throws Exception {
        // Initialize the database
        insertedProductoInventario = productoInventarioRepository.save(productoInventario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoInventario
        ProductoInventario updatedProductoInventario = productoInventarioRepository.findById(productoInventario.getId()).orElseThrow();
        updatedProductoInventario
            .stock(UPDATED_STOCK)
            .stockMinimo(UPDATED_STOCK_MINIMO)
            .ubicacionBodega(UPDATED_UBICACION_BODEGA)
            .garantiaMeses(UPDATED_GARANTIA_MESES);
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(updatedProductoInventario);

        restProductoInventarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoInventarioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoInventarioDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductoInventarioToMatchAllProperties(updatedProductoInventario);
    }

    @Test
    void putNonExistingProductoInventario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoInventario.setId(UUID.randomUUID().toString());

        // Create the ProductoInventario
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoInventarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoInventarioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoInventarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProductoInventario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoInventario.setId(UUID.randomUUID().toString());

        // Create the ProductoInventario
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoInventarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoInventarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProductoInventario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoInventario.setId(UUID.randomUUID().toString());

        // Create the ProductoInventario
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoInventarioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoInventarioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductoInventarioWithPatch() throws Exception {
        // Initialize the database
        insertedProductoInventario = productoInventarioRepository.save(productoInventario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoInventario using partial update
        ProductoInventario partialUpdatedProductoInventario = new ProductoInventario();
        partialUpdatedProductoInventario.setId(productoInventario.getId());

        partialUpdatedProductoInventario.stockMinimo(UPDATED_STOCK_MINIMO).garantiaMeses(UPDATED_GARANTIA_MESES);

        restProductoInventarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductoInventario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductoInventario))
            )
            .andExpect(status().isOk());

        // Validate the ProductoInventario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoInventarioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductoInventario, productoInventario),
            getPersistedProductoInventario(productoInventario)
        );
    }

    @Test
    void fullUpdateProductoInventarioWithPatch() throws Exception {
        // Initialize the database
        insertedProductoInventario = productoInventarioRepository.save(productoInventario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoInventario using partial update
        ProductoInventario partialUpdatedProductoInventario = new ProductoInventario();
        partialUpdatedProductoInventario.setId(productoInventario.getId());

        partialUpdatedProductoInventario
            .stock(UPDATED_STOCK)
            .stockMinimo(UPDATED_STOCK_MINIMO)
            .ubicacionBodega(UPDATED_UBICACION_BODEGA)
            .garantiaMeses(UPDATED_GARANTIA_MESES);

        restProductoInventarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductoInventario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductoInventario))
            )
            .andExpect(status().isOk());

        // Validate the ProductoInventario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoInventarioUpdatableFieldsEquals(
            partialUpdatedProductoInventario,
            getPersistedProductoInventario(partialUpdatedProductoInventario)
        );
    }

    @Test
    void patchNonExistingProductoInventario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoInventario.setId(UUID.randomUUID().toString());

        // Create the ProductoInventario
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoInventarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productoInventarioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoInventarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProductoInventario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoInventario.setId(UUID.randomUUID().toString());

        // Create the ProductoInventario
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoInventarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoInventarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProductoInventario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoInventario.setId(UUID.randomUUID().toString());

        // Create the ProductoInventario
        ProductoInventarioDTO productoInventarioDTO = productoInventarioMapper.toDto(productoInventario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoInventarioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productoInventarioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductoInventario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProductoInventario() throws Exception {
        // Initialize the database
        insertedProductoInventario = productoInventarioRepository.save(productoInventario);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productoInventario
        restProductoInventarioMockMvc
            .perform(delete(ENTITY_API_URL_ID, productoInventario.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productoInventarioRepository.count();
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

    protected ProductoInventario getPersistedProductoInventario(ProductoInventario productoInventario) {
        return productoInventarioRepository.findById(productoInventario.getId()).orElseThrow();
    }

    protected void assertPersistedProductoInventarioToMatchAllProperties(ProductoInventario expectedProductoInventario) {
        assertProductoInventarioAllPropertiesEquals(expectedProductoInventario, getPersistedProductoInventario(expectedProductoInventario));
    }

    protected void assertPersistedProductoInventarioToMatchUpdatableProperties(ProductoInventario expectedProductoInventario) {
        assertProductoInventarioAllUpdatablePropertiesEquals(
            expectedProductoInventario,
            getPersistedProductoInventario(expectedProductoInventario)
        );
    }
}
