package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.ProductoPrecioAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import com.mycompany.knstore.service.mapper.ProductoPrecioMapper;
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
 * Integration tests for the {@link ProductoPrecioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductoPrecioResourceIT {

    private static final BigDecimal DEFAULT_PRECIO_COMPRA = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_COMPRA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_PRECIO_VENTA = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_VENTA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_PRECIO_ADICIONAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_ADICIONAL = new BigDecimal(1);

    private static final BigDecimal DEFAULT_GANANCIA = new BigDecimal(1);
    private static final BigDecimal UPDATED_GANANCIA = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/producto-precios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductoPrecioRepository productoPrecioRepository;

    @Autowired
    private ProductoPrecioMapper productoPrecioMapper;

    @Autowired
    private MockMvc restProductoPrecioMockMvc;

    private ProductoPrecio productoPrecio;

    private ProductoPrecio insertedProductoPrecio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductoPrecio createEntity() {
        return new ProductoPrecio()
            .precioCompra(DEFAULT_PRECIO_COMPRA)
            .precioVenta(DEFAULT_PRECIO_VENTA)
            .precioAdicional(DEFAULT_PRECIO_ADICIONAL)
            .ganancia(DEFAULT_GANANCIA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductoPrecio createUpdatedEntity() {
        return new ProductoPrecio()
            .precioCompra(UPDATED_PRECIO_COMPRA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .precioAdicional(UPDATED_PRECIO_ADICIONAL)
            .ganancia(UPDATED_GANANCIA);
    }

    @BeforeEach
    void initTest() {
        productoPrecio = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductoPrecio != null) {
            productoPrecioRepository.delete(insertedProductoPrecio);
            insertedProductoPrecio = null;
        }
    }

    @Test
    void createProductoPrecio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductoPrecio
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);
        var returnedProductoPrecioDTO = om.readValue(
            restProductoPrecioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoPrecioDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductoPrecioDTO.class
        );

        // Validate the ProductoPrecio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductoPrecio = productoPrecioMapper.toEntity(returnedProductoPrecioDTO);
        assertProductoPrecioUpdatableFieldsEquals(returnedProductoPrecio, getPersistedProductoPrecio(returnedProductoPrecio));

        insertedProductoPrecio = returnedProductoPrecio;
    }

    @Test
    void createProductoPrecioWithExistingId() throws Exception {
        // Create the ProductoPrecio with an existing ID
        productoPrecio.setId("existing_id");
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductoPrecioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoPrecioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkPrecioCompraIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productoPrecio.setPrecioCompra(null);

        // Create the ProductoPrecio, which fails.
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        restProductoPrecioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoPrecioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioVentaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productoPrecio.setPrecioVenta(null);

        // Create the ProductoPrecio, which fails.
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        restProductoPrecioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoPrecioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProductoPrecios() throws Exception {
        // Initialize the database
        insertedProductoPrecio = productoPrecioRepository.save(productoPrecio);

        // Get all the productoPrecioList
        restProductoPrecioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productoPrecio.getId())))
            .andExpect(jsonPath("$.[*].precioCompra").value(hasItem(sameNumber(DEFAULT_PRECIO_COMPRA))))
            .andExpect(jsonPath("$.[*].precioVenta").value(hasItem(sameNumber(DEFAULT_PRECIO_VENTA))))
            .andExpect(jsonPath("$.[*].precioAdicional").value(hasItem(sameNumber(DEFAULT_PRECIO_ADICIONAL))))
            .andExpect(jsonPath("$.[*].ganancia").value(hasItem(sameNumber(DEFAULT_GANANCIA))));
    }

    @Test
    void getProductoPrecio() throws Exception {
        // Initialize the database
        insertedProductoPrecio = productoPrecioRepository.save(productoPrecio);

        // Get the productoPrecio
        restProductoPrecioMockMvc
            .perform(get(ENTITY_API_URL_ID, productoPrecio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productoPrecio.getId()))
            .andExpect(jsonPath("$.precioCompra").value(sameNumber(DEFAULT_PRECIO_COMPRA)))
            .andExpect(jsonPath("$.precioVenta").value(sameNumber(DEFAULT_PRECIO_VENTA)))
            .andExpect(jsonPath("$.precioAdicional").value(sameNumber(DEFAULT_PRECIO_ADICIONAL)))
            .andExpect(jsonPath("$.ganancia").value(sameNumber(DEFAULT_GANANCIA)));
    }

    @Test
    void getNonExistingProductoPrecio() throws Exception {
        // Get the productoPrecio
        restProductoPrecioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingProductoPrecio() throws Exception {
        // Initialize the database
        insertedProductoPrecio = productoPrecioRepository.save(productoPrecio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoPrecio
        ProductoPrecio updatedProductoPrecio = productoPrecioRepository.findById(productoPrecio.getId()).orElseThrow();
        updatedProductoPrecio
            .precioCompra(UPDATED_PRECIO_COMPRA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .precioAdicional(UPDATED_PRECIO_ADICIONAL)
            .ganancia(UPDATED_GANANCIA);
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(updatedProductoPrecio);

        restProductoPrecioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoPrecioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoPrecioDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductoPrecioToMatchAllProperties(updatedProductoPrecio);
    }

    @Test
    void putNonExistingProductoPrecio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoPrecio.setId(UUID.randomUUID().toString());

        // Create the ProductoPrecio
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoPrecioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoPrecioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoPrecioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProductoPrecio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoPrecio.setId(UUID.randomUUID().toString());

        // Create the ProductoPrecio
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoPrecioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoPrecioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProductoPrecio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoPrecio.setId(UUID.randomUUID().toString());

        // Create the ProductoPrecio
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoPrecioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoPrecioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductoPrecioWithPatch() throws Exception {
        // Initialize the database
        insertedProductoPrecio = productoPrecioRepository.save(productoPrecio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoPrecio using partial update
        ProductoPrecio partialUpdatedProductoPrecio = new ProductoPrecio();
        partialUpdatedProductoPrecio.setId(productoPrecio.getId());

        partialUpdatedProductoPrecio.precioCompra(UPDATED_PRECIO_COMPRA).precioVenta(UPDATED_PRECIO_VENTA).ganancia(UPDATED_GANANCIA);

        restProductoPrecioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductoPrecio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductoPrecio))
            )
            .andExpect(status().isOk());

        // Validate the ProductoPrecio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoPrecioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductoPrecio, productoPrecio),
            getPersistedProductoPrecio(productoPrecio)
        );
    }

    @Test
    void fullUpdateProductoPrecioWithPatch() throws Exception {
        // Initialize the database
        insertedProductoPrecio = productoPrecioRepository.save(productoPrecio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productoPrecio using partial update
        ProductoPrecio partialUpdatedProductoPrecio = new ProductoPrecio();
        partialUpdatedProductoPrecio.setId(productoPrecio.getId());

        partialUpdatedProductoPrecio
            .precioCompra(UPDATED_PRECIO_COMPRA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .precioAdicional(UPDATED_PRECIO_ADICIONAL)
            .ganancia(UPDATED_GANANCIA);

        restProductoPrecioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductoPrecio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductoPrecio))
            )
            .andExpect(status().isOk());

        // Validate the ProductoPrecio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoPrecioUpdatableFieldsEquals(partialUpdatedProductoPrecio, getPersistedProductoPrecio(partialUpdatedProductoPrecio));
    }

    @Test
    void patchNonExistingProductoPrecio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoPrecio.setId(UUID.randomUUID().toString());

        // Create the ProductoPrecio
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoPrecioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productoPrecioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoPrecioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProductoPrecio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoPrecio.setId(UUID.randomUUID().toString());

        // Create the ProductoPrecio
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoPrecioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoPrecioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProductoPrecio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productoPrecio.setId(UUID.randomUUID().toString());

        // Create the ProductoPrecio
        ProductoPrecioDTO productoPrecioDTO = productoPrecioMapper.toDto(productoPrecio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoPrecioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productoPrecioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductoPrecio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProductoPrecio() throws Exception {
        // Initialize the database
        insertedProductoPrecio = productoPrecioRepository.save(productoPrecio);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productoPrecio
        restProductoPrecioMockMvc
            .perform(delete(ENTITY_API_URL_ID, productoPrecio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productoPrecioRepository.count();
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

    protected ProductoPrecio getPersistedProductoPrecio(ProductoPrecio productoPrecio) {
        return productoPrecioRepository.findById(productoPrecio.getId()).orElseThrow();
    }

    protected void assertPersistedProductoPrecioToMatchAllProperties(ProductoPrecio expectedProductoPrecio) {
        assertProductoPrecioAllPropertiesEquals(expectedProductoPrecio, getPersistedProductoPrecio(expectedProductoPrecio));
    }

    protected void assertPersistedProductoPrecioToMatchUpdatableProperties(ProductoPrecio expectedProductoPrecio) {
        assertProductoPrecioAllUpdatablePropertiesEquals(expectedProductoPrecio, getPersistedProductoPrecio(expectedProductoPrecio));
    }
}
