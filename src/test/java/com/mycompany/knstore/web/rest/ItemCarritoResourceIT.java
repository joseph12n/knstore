package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.ItemCarritoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.service.ItemCarritoService;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.mapper.ItemCarritoMapper;
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
 * Integration tests for the {@link ItemCarritoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ItemCarritoResourceIT {

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;

    private static final BigDecimal DEFAULT_PRECIO_UNITARIO = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_UNITARIO = new BigDecimal(1);

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/item-carritos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepositoryMock;

    @Autowired
    private ItemCarritoMapper itemCarritoMapper;

    @Mock
    private ItemCarritoService itemCarritoServiceMock;

    @Autowired
    private MockMvc restItemCarritoMockMvc;

    private ItemCarrito itemCarrito;

    private ItemCarrito insertedItemCarrito;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemCarrito createEntity() {
        ItemCarrito itemCarrito = new ItemCarrito()
            .cantidad(DEFAULT_CANTIDAD)
            .precioUnitario(DEFAULT_PRECIO_UNITARIO)
            .subtotal(DEFAULT_SUBTOTAL);
        // Add required entity
        Carrito carrito;
        carrito = CarritoResourceIT.createEntity();
        carrito.setId("fixed-id-for-tests");
        itemCarrito.setCarrito(carrito);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createEntity();
        producto.setId("fixed-id-for-tests");
        itemCarrito.setProducto(producto);
        return itemCarrito;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemCarrito createUpdatedEntity() {
        ItemCarrito updatedItemCarrito = new ItemCarrito()
            .cantidad(UPDATED_CANTIDAD)
            .precioUnitario(UPDATED_PRECIO_UNITARIO)
            .subtotal(UPDATED_SUBTOTAL);
        // Add required entity
        Carrito carrito;
        carrito = CarritoResourceIT.createUpdatedEntity();
        carrito.setId("fixed-id-for-tests");
        updatedItemCarrito.setCarrito(carrito);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createUpdatedEntity();
        producto.setId("fixed-id-for-tests");
        updatedItemCarrito.setProducto(producto);
        return updatedItemCarrito;
    }

    @BeforeEach
    void initTest() {
        itemCarrito = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedItemCarrito != null) {
            itemCarritoRepository.delete(insertedItemCarrito);
            insertedItemCarrito = null;
        }
    }

    @Test
    void createItemCarrito() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ItemCarrito
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);
        var returnedItemCarritoDTO = om.readValue(
            restItemCarritoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemCarritoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ItemCarritoDTO.class
        );

        // Validate the ItemCarrito in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedItemCarrito = itemCarritoMapper.toEntity(returnedItemCarritoDTO);
        assertItemCarritoUpdatableFieldsEquals(returnedItemCarrito, getPersistedItemCarrito(returnedItemCarrito));

        insertedItemCarrito = returnedItemCarrito;
    }

    @Test
    void createItemCarritoWithExistingId() throws Exception {
        // Create the ItemCarrito with an existing ID
        itemCarrito.setId("existing_id");
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restItemCarritoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemCarritoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCantidadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        itemCarrito.setCantidad(null);

        // Create the ItemCarrito, which fails.
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        restItemCarritoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemCarritoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioUnitarioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        itemCarrito.setPrecioUnitario(null);

        // Create the ItemCarrito, which fails.
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        restItemCarritoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemCarritoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllItemCarritos() throws Exception {
        // Initialize the database
        insertedItemCarrito = itemCarritoRepository.save(itemCarrito);

        // Get all the itemCarritoList
        restItemCarritoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(itemCarrito.getId())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD)))
            .andExpect(jsonPath("$.[*].precioUnitario").value(hasItem(sameNumber(DEFAULT_PRECIO_UNITARIO))))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllItemCarritosWithEagerRelationshipsIsEnabled() throws Exception {
        when(itemCarritoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restItemCarritoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(itemCarritoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllItemCarritosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(itemCarritoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restItemCarritoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(itemCarritoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getItemCarrito() throws Exception {
        // Initialize the database
        insertedItemCarrito = itemCarritoRepository.save(itemCarrito);

        // Get the itemCarrito
        restItemCarritoMockMvc
            .perform(get(ENTITY_API_URL_ID, itemCarrito.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(itemCarrito.getId()))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD))
            .andExpect(jsonPath("$.precioUnitario").value(sameNumber(DEFAULT_PRECIO_UNITARIO)))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)));
    }

    @Test
    void getNonExistingItemCarrito() throws Exception {
        // Get the itemCarrito
        restItemCarritoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingItemCarrito() throws Exception {
        // Initialize the database
        insertedItemCarrito = itemCarritoRepository.save(itemCarrito);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemCarrito
        ItemCarrito updatedItemCarrito = itemCarritoRepository.findById(itemCarrito.getId()).orElseThrow();
        updatedItemCarrito.cantidad(UPDATED_CANTIDAD).precioUnitario(UPDATED_PRECIO_UNITARIO).subtotal(UPDATED_SUBTOTAL);
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(updatedItemCarrito);

        restItemCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, itemCarritoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(itemCarritoDTO))
            )
            .andExpect(status().isOk());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedItemCarritoToMatchAllProperties(updatedItemCarrito);
    }

    @Test
    void putNonExistingItemCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCarrito.setId(UUID.randomUUID().toString());

        // Create the ItemCarrito
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, itemCarritoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(itemCarritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchItemCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCarrito.setId(UUID.randomUUID().toString());

        // Create the ItemCarrito
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(itemCarritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamItemCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCarrito.setId(UUID.randomUUID().toString());

        // Create the ItemCarrito
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarritoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemCarritoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateItemCarritoWithPatch() throws Exception {
        // Initialize the database
        insertedItemCarrito = itemCarritoRepository.save(itemCarrito);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemCarrito using partial update
        ItemCarrito partialUpdatedItemCarrito = new ItemCarrito();
        partialUpdatedItemCarrito.setId(itemCarrito.getId());

        partialUpdatedItemCarrito.subtotal(UPDATED_SUBTOTAL);

        restItemCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItemCarrito.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedItemCarrito))
            )
            .andExpect(status().isOk());

        // Validate the ItemCarrito in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertItemCarritoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedItemCarrito, itemCarrito),
            getPersistedItemCarrito(itemCarrito)
        );
    }

    @Test
    void fullUpdateItemCarritoWithPatch() throws Exception {
        // Initialize the database
        insertedItemCarrito = itemCarritoRepository.save(itemCarrito);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemCarrito using partial update
        ItemCarrito partialUpdatedItemCarrito = new ItemCarrito();
        partialUpdatedItemCarrito.setId(itemCarrito.getId());

        partialUpdatedItemCarrito.cantidad(UPDATED_CANTIDAD).precioUnitario(UPDATED_PRECIO_UNITARIO).subtotal(UPDATED_SUBTOTAL);

        restItemCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItemCarrito.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedItemCarrito))
            )
            .andExpect(status().isOk());

        // Validate the ItemCarrito in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertItemCarritoUpdatableFieldsEquals(partialUpdatedItemCarrito, getPersistedItemCarrito(partialUpdatedItemCarrito));
    }

    @Test
    void patchNonExistingItemCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCarrito.setId(UUID.randomUUID().toString());

        // Create the ItemCarrito
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, itemCarritoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(itemCarritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchItemCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCarrito.setId(UUID.randomUUID().toString());

        // Create the ItemCarrito
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(itemCarritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamItemCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemCarrito.setId(UUID.randomUUID().toString());

        // Create the ItemCarrito
        ItemCarritoDTO itemCarritoDTO = itemCarritoMapper.toDto(itemCarrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemCarritoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(itemCarritoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ItemCarrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteItemCarrito() throws Exception {
        // Initialize the database
        insertedItemCarrito = itemCarritoRepository.save(itemCarrito);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the itemCarrito
        restItemCarritoMockMvc
            .perform(delete(ENTITY_API_URL_ID, itemCarrito.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return itemCarritoRepository.count();
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

    protected ItemCarrito getPersistedItemCarrito(ItemCarrito itemCarrito) {
        return itemCarritoRepository.findById(itemCarrito.getId()).orElseThrow();
    }

    protected void assertPersistedItemCarritoToMatchAllProperties(ItemCarrito expectedItemCarrito) {
        assertItemCarritoAllPropertiesEquals(expectedItemCarrito, getPersistedItemCarrito(expectedItemCarrito));
    }

    protected void assertPersistedItemCarritoToMatchUpdatableProperties(ItemCarrito expectedItemCarrito) {
        assertItemCarritoAllUpdatablePropertiesEquals(expectedItemCarrito, getPersistedItemCarrito(expectedItemCarrito));
    }
}
