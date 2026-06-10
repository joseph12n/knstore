package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.VarianteProductoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.VarianteProducto;
import com.mycompany.knstore.repository.VarianteProductoRepository;
import com.mycompany.knstore.service.VarianteProductoService;
import com.mycompany.knstore.service.dto.VarianteProductoDTO;
import com.mycompany.knstore.service.mapper.VarianteProductoMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link VarianteProductoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VarianteProductoResourceIT {

    private static final String DEFAULT_SKU = "AAAAAAAAAA";
    private static final String UPDATED_SKU = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final String DEFAULT_TALLA = "AAAAAAAAAA";
    private static final String UPDATED_TALLA = "BBBBBBBBBB";

    private static final String DEFAULT_CODIGO_BARRAS = "AAAAAAAAAA";
    private static final String UPDATED_CODIGO_BARRAS = "BBBBBBBBBB";

    private static final Integer DEFAULT_STOCK = 0;
    private static final Integer UPDATED_STOCK = 1;

    private static final Integer DEFAULT_STOCK_MINIMO = 0;
    private static final Integer UPDATED_STOCK_MINIMO = 1;

    private static final String DEFAULT_UBICACION_BODEGA = "AAAAAAAAAA";
    private static final String UPDATED_UBICACION_BODEGA = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRECIO_ADICIONAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_ADICIONAL = new BigDecimal(1);

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final String ENTITY_API_URL = "/api/variante-productos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VarianteProductoRepository varianteProductoRepository;

    @Mock
    private VarianteProductoRepository varianteProductoRepositoryMock;

    @Autowired
    private VarianteProductoMapper varianteProductoMapper;

    @Mock
    private VarianteProductoService varianteProductoServiceMock;

    @Autowired
    private MockMvc restVarianteProductoMockMvc;

    private VarianteProducto varianteProducto;

    private VarianteProducto insertedVarianteProducto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VarianteProducto createEntity() {
        VarianteProducto varianteProducto = new VarianteProducto()
            .sku(DEFAULT_SKU)
            .color(DEFAULT_COLOR)
            .talla(DEFAULT_TALLA)
            .codigoBarras(DEFAULT_CODIGO_BARRAS)
            .stock(DEFAULT_STOCK)
            .stockMinimo(DEFAULT_STOCK_MINIMO)
            .ubicacionBodega(DEFAULT_UBICACION_BODEGA)
            .precioAdicional(DEFAULT_PRECIO_ADICIONAL)
            .activo(DEFAULT_ACTIVO);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createEntity();
        producto.setId("fixed-id-for-tests");
        varianteProducto.setProducto(producto);
        return varianteProducto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VarianteProducto createUpdatedEntity() {
        VarianteProducto updatedVarianteProducto = new VarianteProducto()
            .sku(UPDATED_SKU)
            .color(UPDATED_COLOR)
            .talla(UPDATED_TALLA)
            .codigoBarras(UPDATED_CODIGO_BARRAS)
            .stock(UPDATED_STOCK)
            .stockMinimo(UPDATED_STOCK_MINIMO)
            .ubicacionBodega(UPDATED_UBICACION_BODEGA)
            .precioAdicional(UPDATED_PRECIO_ADICIONAL)
            .activo(UPDATED_ACTIVO);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createUpdatedEntity();
        producto.setId("fixed-id-for-tests");
        updatedVarianteProducto.setProducto(producto);
        return updatedVarianteProducto;
    }

    @BeforeEach
    void initTest() {
        varianteProducto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVarianteProducto != null) {
            varianteProductoRepository.delete(insertedVarianteProducto);
            insertedVarianteProducto = null;
        }
    }

    @Test
    void createVarianteProducto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VarianteProducto
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);
        var returnedVarianteProductoDTO = om.readValue(
            restVarianteProductoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProductoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VarianteProductoDTO.class
        );

        // Validate the VarianteProducto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVarianteProducto = varianteProductoMapper.toEntity(returnedVarianteProductoDTO);
        assertVarianteProductoUpdatableFieldsEquals(returnedVarianteProducto, getPersistedVarianteProducto(returnedVarianteProducto));

        insertedVarianteProducto = returnedVarianteProducto;
    }

    @Test
    void createVarianteProductoWithExistingId() throws Exception {
        // Create the VarianteProducto with an existing ID
        varianteProducto.setId("existing_id");
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVarianteProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProductoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStockIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        varianteProducto.setStock(null);

        // Create the VarianteProducto, which fails.
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        restVarianteProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProductoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        varianteProducto.setActivo(null);

        // Create the VarianteProducto, which fails.
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        restVarianteProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProductoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllVarianteProductos() throws Exception {
        // Initialize the database
        insertedVarianteProducto = varianteProductoRepository.save(varianteProducto);

        // Get all the varianteProductoList
        restVarianteProductoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(varianteProducto.getId())))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].talla").value(hasItem(DEFAULT_TALLA)))
            .andExpect(jsonPath("$.[*].codigoBarras").value(hasItem(DEFAULT_CODIGO_BARRAS)))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK)))
            .andExpect(jsonPath("$.[*].stockMinimo").value(hasItem(DEFAULT_STOCK_MINIMO)))
            .andExpect(jsonPath("$.[*].ubicacionBodega").value(hasItem(DEFAULT_UBICACION_BODEGA)))
            .andExpect(jsonPath("$.[*].precioAdicional").value(hasItem(sameNumber(DEFAULT_PRECIO_ADICIONAL))))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVarianteProductosWithEagerRelationshipsIsEnabled() throws Exception {
        when(varianteProductoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVarianteProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(varianteProductoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVarianteProductosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(varianteProductoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVarianteProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(varianteProductoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getVarianteProducto() throws Exception {
        // Initialize the database
        insertedVarianteProducto = varianteProductoRepository.save(varianteProducto);

        // Get the varianteProducto
        restVarianteProductoMockMvc
            .perform(get(ENTITY_API_URL_ID, varianteProducto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(varianteProducto.getId()))
            .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR))
            .andExpect(jsonPath("$.talla").value(DEFAULT_TALLA))
            .andExpect(jsonPath("$.codigoBarras").value(DEFAULT_CODIGO_BARRAS))
            .andExpect(jsonPath("$.stock").value(DEFAULT_STOCK))
            .andExpect(jsonPath("$.stockMinimo").value(DEFAULT_STOCK_MINIMO))
            .andExpect(jsonPath("$.ubicacionBodega").value(DEFAULT_UBICACION_BODEGA))
            .andExpect(jsonPath("$.precioAdicional").value(sameNumber(DEFAULT_PRECIO_ADICIONAL)))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO));
    }

    @Test
    void getNonExistingVarianteProducto() throws Exception {
        // Get the varianteProducto
        restVarianteProductoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingVarianteProducto() throws Exception {
        // Initialize the database
        insertedVarianteProducto = varianteProductoRepository.save(varianteProducto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the varianteProducto
        VarianteProducto updatedVarianteProducto = varianteProductoRepository.findById(varianteProducto.getId()).orElseThrow();
        updatedVarianteProducto
            .sku(UPDATED_SKU)
            .color(UPDATED_COLOR)
            .talla(UPDATED_TALLA)
            .codigoBarras(UPDATED_CODIGO_BARRAS)
            .stock(UPDATED_STOCK)
            .stockMinimo(UPDATED_STOCK_MINIMO)
            .ubicacionBodega(UPDATED_UBICACION_BODEGA)
            .precioAdicional(UPDATED_PRECIO_ADICIONAL)
            .activo(UPDATED_ACTIVO);
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(updatedVarianteProducto);

        restVarianteProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, varianteProductoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(varianteProductoDTO))
            )
            .andExpect(status().isOk());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVarianteProductoToMatchAllProperties(updatedVarianteProducto);
    }

    @Test
    void putNonExistingVarianteProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProducto.setId(UUID.randomUUID().toString());

        // Create the VarianteProducto
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVarianteProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, varianteProductoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(varianteProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVarianteProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProducto.setId(UUID.randomUUID().toString());

        // Create the VarianteProducto
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(varianteProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVarianteProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProducto.setId(UUID.randomUUID().toString());

        // Create the VarianteProducto
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProductoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProductoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVarianteProductoWithPatch() throws Exception {
        // Initialize the database
        insertedVarianteProducto = varianteProductoRepository.save(varianteProducto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the varianteProducto using partial update
        VarianteProducto partialUpdatedVarianteProducto = new VarianteProducto();
        partialUpdatedVarianteProducto.setId(varianteProducto.getId());

        partialUpdatedVarianteProducto.color(UPDATED_COLOR).stock(UPDATED_STOCK).ubicacionBodega(UPDATED_UBICACION_BODEGA);

        restVarianteProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVarianteProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVarianteProducto))
            )
            .andExpect(status().isOk());

        // Validate the VarianteProducto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVarianteProductoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVarianteProducto, varianteProducto),
            getPersistedVarianteProducto(varianteProducto)
        );
    }

    @Test
    void fullUpdateVarianteProductoWithPatch() throws Exception {
        // Initialize the database
        insertedVarianteProducto = varianteProductoRepository.save(varianteProducto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the varianteProducto using partial update
        VarianteProducto partialUpdatedVarianteProducto = new VarianteProducto();
        partialUpdatedVarianteProducto.setId(varianteProducto.getId());

        partialUpdatedVarianteProducto
            .sku(UPDATED_SKU)
            .color(UPDATED_COLOR)
            .talla(UPDATED_TALLA)
            .codigoBarras(UPDATED_CODIGO_BARRAS)
            .stock(UPDATED_STOCK)
            .stockMinimo(UPDATED_STOCK_MINIMO)
            .ubicacionBodega(UPDATED_UBICACION_BODEGA)
            .precioAdicional(UPDATED_PRECIO_ADICIONAL)
            .activo(UPDATED_ACTIVO);

        restVarianteProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVarianteProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVarianteProducto))
            )
            .andExpect(status().isOk());

        // Validate the VarianteProducto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVarianteProductoUpdatableFieldsEquals(
            partialUpdatedVarianteProducto,
            getPersistedVarianteProducto(partialUpdatedVarianteProducto)
        );
    }

    @Test
    void patchNonExistingVarianteProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProducto.setId(UUID.randomUUID().toString());

        // Create the VarianteProducto
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVarianteProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, varianteProductoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(varianteProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVarianteProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProducto.setId(UUID.randomUUID().toString());

        // Create the VarianteProducto
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(varianteProductoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVarianteProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProducto.setId(UUID.randomUUID().toString());

        // Create the VarianteProducto
        VarianteProductoDTO varianteProductoDTO = varianteProductoMapper.toDto(varianteProducto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProductoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(varianteProductoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VarianteProducto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVarianteProducto() throws Exception {
        // Initialize the database
        insertedVarianteProducto = varianteProductoRepository.save(varianteProducto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the varianteProducto
        restVarianteProductoMockMvc
            .perform(delete(ENTITY_API_URL_ID, varianteProducto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return varianteProductoRepository.count();
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

    protected VarianteProducto getPersistedVarianteProducto(VarianteProducto varianteProducto) {
        return varianteProductoRepository.findById(varianteProducto.getId()).orElseThrow();
    }

    protected void assertPersistedVarianteProductoToMatchAllProperties(VarianteProducto expectedVarianteProducto) {
        assertVarianteProductoAllPropertiesEquals(expectedVarianteProducto, getPersistedVarianteProducto(expectedVarianteProducto));
    }

    protected void assertPersistedVarianteProductoToMatchUpdatableProperties(VarianteProducto expectedVarianteProducto) {
        assertVarianteProductoAllUpdatablePropertiesEquals(
            expectedVarianteProducto,
            getPersistedVarianteProducto(expectedVarianteProducto)
        );
    }
}
