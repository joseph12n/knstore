package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.ItemPedidoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.service.ItemPedidoService;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
import com.mycompany.knstore.service.mapper.ItemPedidoMapper;
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
 * Integration tests for the {@link ItemPedidoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ItemPedidoResourceIT {

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;

    private static final BigDecimal DEFAULT_PRECIO_UNITARIO = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_UNITARIO = new BigDecimal(1);

    private static final BigDecimal DEFAULT_PORCENTAJE_IVA = new BigDecimal(0);
    private static final BigDecimal UPDATED_PORCENTAJE_IVA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_VALOR_IVA = new BigDecimal(0);
    private static final BigDecimal UPDATED_VALOR_IVA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_DESCUENTO = new BigDecimal(0);
    private static final BigDecimal UPDATED_DESCUENTO = new BigDecimal(1);

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/item-pedidos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepositoryMock;

    @Autowired
    private ItemPedidoMapper itemPedidoMapper;

    @Mock
    private ItemPedidoService itemPedidoServiceMock;

    @Autowired
    private MockMvc restItemPedidoMockMvc;

    private ItemPedido itemPedido;

    private ItemPedido insertedItemPedido;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemPedido createEntity() {
        ItemPedido itemPedido = new ItemPedido()
            .cantidad(DEFAULT_CANTIDAD)
            .precioUnitario(DEFAULT_PRECIO_UNITARIO)
            .porcentajeIva(DEFAULT_PORCENTAJE_IVA)
            .valorIva(DEFAULT_VALOR_IVA)
            .descuento(DEFAULT_DESCUENTO)
            .subtotal(DEFAULT_SUBTOTAL);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createEntity();
        pedido.setId("fixed-id-for-tests");
        itemPedido.setPedido(pedido);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createEntity();
        producto.setId("fixed-id-for-tests");
        itemPedido.setProducto(producto);
        return itemPedido;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ItemPedido createUpdatedEntity() {
        ItemPedido updatedItemPedido = new ItemPedido()
            .cantidad(UPDATED_CANTIDAD)
            .precioUnitario(UPDATED_PRECIO_UNITARIO)
            .porcentajeIva(UPDATED_PORCENTAJE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .descuento(UPDATED_DESCUENTO)
            .subtotal(UPDATED_SUBTOTAL);
        // Add required entity
        Pedido pedido;
        pedido = PedidoResourceIT.createUpdatedEntity();
        pedido.setId("fixed-id-for-tests");
        updatedItemPedido.setPedido(pedido);
        // Add required entity
        Producto producto;
        producto = ProductoResourceIT.createUpdatedEntity();
        producto.setId("fixed-id-for-tests");
        updatedItemPedido.setProducto(producto);
        return updatedItemPedido;
    }

    @BeforeEach
    void initTest() {
        itemPedido = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedItemPedido != null) {
            itemPedidoRepository.delete(insertedItemPedido);
            insertedItemPedido = null;
        }
    }

    @Test
    void createItemPedido() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ItemPedido
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);
        var returnedItemPedidoDTO = om.readValue(
            restItemPedidoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemPedidoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ItemPedidoDTO.class
        );

        // Validate the ItemPedido in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedItemPedido = itemPedidoMapper.toEntity(returnedItemPedidoDTO);
        assertItemPedidoUpdatableFieldsEquals(returnedItemPedido, getPersistedItemPedido(returnedItemPedido));

        insertedItemPedido = returnedItemPedido;
    }

    @Test
    void createItemPedidoWithExistingId() throws Exception {
        // Create the ItemPedido with an existing ID
        itemPedido.setId("existing_id");
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restItemPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemPedidoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCantidadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        itemPedido.setCantidad(null);

        // Create the ItemPedido, which fails.
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        restItemPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemPedidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioUnitarioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        itemPedido.setPrecioUnitario(null);

        // Create the ItemPedido, which fails.
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        restItemPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemPedidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllItemPedidos() throws Exception {
        // Initialize the database
        insertedItemPedido = itemPedidoRepository.save(itemPedido);

        // Get all the itemPedidoList
        restItemPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(itemPedido.getId())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD)))
            .andExpect(jsonPath("$.[*].precioUnitario").value(hasItem(sameNumber(DEFAULT_PRECIO_UNITARIO))))
            .andExpect(jsonPath("$.[*].porcentajeIva").value(hasItem(sameNumber(DEFAULT_PORCENTAJE_IVA))))
            .andExpect(jsonPath("$.[*].valorIva").value(hasItem(sameNumber(DEFAULT_VALOR_IVA))))
            .andExpect(jsonPath("$.[*].descuento").value(hasItem(sameNumber(DEFAULT_DESCUENTO))))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllItemPedidosWithEagerRelationshipsIsEnabled() throws Exception {
        when(itemPedidoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restItemPedidoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(itemPedidoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllItemPedidosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(itemPedidoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restItemPedidoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(itemPedidoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getItemPedido() throws Exception {
        // Initialize the database
        insertedItemPedido = itemPedidoRepository.save(itemPedido);

        // Get the itemPedido
        restItemPedidoMockMvc
            .perform(get(ENTITY_API_URL_ID, itemPedido.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(itemPedido.getId()))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD))
            .andExpect(jsonPath("$.precioUnitario").value(sameNumber(DEFAULT_PRECIO_UNITARIO)))
            .andExpect(jsonPath("$.porcentajeIva").value(sameNumber(DEFAULT_PORCENTAJE_IVA)))
            .andExpect(jsonPath("$.valorIva").value(sameNumber(DEFAULT_VALOR_IVA)))
            .andExpect(jsonPath("$.descuento").value(sameNumber(DEFAULT_DESCUENTO)))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)));
    }

    @Test
    void getNonExistingItemPedido() throws Exception {
        // Get the itemPedido
        restItemPedidoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingItemPedido() throws Exception {
        // Initialize the database
        insertedItemPedido = itemPedidoRepository.save(itemPedido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemPedido
        ItemPedido updatedItemPedido = itemPedidoRepository.findById(itemPedido.getId()).orElseThrow();
        updatedItemPedido
            .cantidad(UPDATED_CANTIDAD)
            .precioUnitario(UPDATED_PRECIO_UNITARIO)
            .porcentajeIva(UPDATED_PORCENTAJE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .descuento(UPDATED_DESCUENTO)
            .subtotal(UPDATED_SUBTOTAL);
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(updatedItemPedido);

        restItemPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, itemPedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(itemPedidoDTO))
            )
            .andExpect(status().isOk());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedItemPedidoToMatchAllProperties(updatedItemPedido);
    }

    @Test
    void putNonExistingItemPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemPedido.setId(UUID.randomUUID().toString());

        // Create the ItemPedido
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, itemPedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(itemPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchItemPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemPedido.setId(UUID.randomUUID().toString());

        // Create the ItemPedido
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(itemPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamItemPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemPedido.setId(UUID.randomUUID().toString());

        // Create the ItemPedido
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemPedidoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(itemPedidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateItemPedidoWithPatch() throws Exception {
        // Initialize the database
        insertedItemPedido = itemPedidoRepository.save(itemPedido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemPedido using partial update
        ItemPedido partialUpdatedItemPedido = new ItemPedido();
        partialUpdatedItemPedido.setId(itemPedido.getId());

        partialUpdatedItemPedido.cantidad(UPDATED_CANTIDAD).porcentajeIva(UPDATED_PORCENTAJE_IVA).valorIva(UPDATED_VALOR_IVA);

        restItemPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItemPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedItemPedido))
            )
            .andExpect(status().isOk());

        // Validate the ItemPedido in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertItemPedidoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedItemPedido, itemPedido),
            getPersistedItemPedido(itemPedido)
        );
    }

    @Test
    void fullUpdateItemPedidoWithPatch() throws Exception {
        // Initialize the database
        insertedItemPedido = itemPedidoRepository.save(itemPedido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the itemPedido using partial update
        ItemPedido partialUpdatedItemPedido = new ItemPedido();
        partialUpdatedItemPedido.setId(itemPedido.getId());

        partialUpdatedItemPedido
            .cantidad(UPDATED_CANTIDAD)
            .precioUnitario(UPDATED_PRECIO_UNITARIO)
            .porcentajeIva(UPDATED_PORCENTAJE_IVA)
            .valorIva(UPDATED_VALOR_IVA)
            .descuento(UPDATED_DESCUENTO)
            .subtotal(UPDATED_SUBTOTAL);

        restItemPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedItemPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedItemPedido))
            )
            .andExpect(status().isOk());

        // Validate the ItemPedido in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertItemPedidoUpdatableFieldsEquals(partialUpdatedItemPedido, getPersistedItemPedido(partialUpdatedItemPedido));
    }

    @Test
    void patchNonExistingItemPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemPedido.setId(UUID.randomUUID().toString());

        // Create the ItemPedido
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, itemPedidoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(itemPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchItemPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemPedido.setId(UUID.randomUUID().toString());

        // Create the ItemPedido
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(itemPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamItemPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        itemPedido.setId(UUID.randomUUID().toString());

        // Create the ItemPedido
        ItemPedidoDTO itemPedidoDTO = itemPedidoMapper.toDto(itemPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restItemPedidoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(itemPedidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ItemPedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteItemPedido() throws Exception {
        // Initialize the database
        insertedItemPedido = itemPedidoRepository.save(itemPedido);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the itemPedido
        restItemPedidoMockMvc
            .perform(delete(ENTITY_API_URL_ID, itemPedido.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return itemPedidoRepository.count();
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

    protected ItemPedido getPersistedItemPedido(ItemPedido itemPedido) {
        return itemPedidoRepository.findById(itemPedido.getId()).orElseThrow();
    }

    protected void assertPersistedItemPedidoToMatchAllProperties(ItemPedido expectedItemPedido) {
        assertItemPedidoAllPropertiesEquals(expectedItemPedido, getPersistedItemPedido(expectedItemPedido));
    }

    protected void assertPersistedItemPedidoToMatchUpdatableProperties(ItemPedido expectedItemPedido) {
        assertItemPedidoAllUpdatablePropertiesEquals(expectedItemPedido, getPersistedItemPedido(expectedItemPedido));
    }
}
