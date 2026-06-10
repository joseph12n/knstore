package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.PedidoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.mapper.PedidoMapper;
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
 * Integration tests for the {@link PedidoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PedidoResourceIT {

    private static final String DEFAULT_NUMERO_PEDIDO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_PEDIDO = "BBBBBBBBBB";

    private static final EstadoPedido DEFAULT_ESTADO = EstadoPedido.PENDING;
    private static final EstadoPedido UPDATED_ESTADO = EstadoPedido.CONFIRMED;

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);

    private static final BigDecimal DEFAULT_DESCUENTO = new BigDecimal(0);
    private static final BigDecimal UPDATED_DESCUENTO = new BigDecimal(1);

    private static final BigDecimal DEFAULT_IVA_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_IVA_TOTAL = new BigDecimal(1);

    private static final BigDecimal DEFAULT_COSTO_ENVIO = new BigDecimal(0);
    private static final BigDecimal UPDATED_COSTO_ENVIO = new BigDecimal(1);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(1);

    private static final String DEFAULT_NOTAS_CLIENTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTAS_CLIENTE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTAS_INTERNAS = "AAAAAAAAAA";
    private static final String UPDATED_NOTAS_INTERNAS = "BBBBBBBBBB";

    private static final String DEFAULT_IP_ORIGEN = "AAAAAAAAAA";
    private static final String UPDATED_IP_ORIGEN = "BBBBBBBBBB";

    private static final String DEFAULT_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_USER_AGENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pedidos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Autowired
    private MockMvc restPedidoMockMvc;

    private Pedido pedido;

    private Pedido insertedPedido;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pedido createEntity() {
        Pedido pedido = new Pedido()
            .numeroPedido(DEFAULT_NUMERO_PEDIDO)
            .estado(DEFAULT_ESTADO)
            .subtotal(DEFAULT_SUBTOTAL)
            .descuento(DEFAULT_DESCUENTO)
            .ivaTotal(DEFAULT_IVA_TOTAL)
            .costoEnvio(DEFAULT_COSTO_ENVIO)
            .total(DEFAULT_TOTAL)
            .notasCliente(DEFAULT_NOTAS_CLIENTE)
            .notasInternas(DEFAULT_NOTAS_INTERNAS)
            .ipOrigen(DEFAULT_IP_ORIGEN)
            .userAgent(DEFAULT_USER_AGENT);
        // Add required entity
        Direccion direccion;
        direccion = DireccionResourceIT.createEntity();
        direccion.setId("fixed-id-for-tests");
        pedido.setDireccion(direccion);
        // Add required entity
        Envio envio;
        envio = EnvioResourceIT.createEntity();
        envio.setId("fixed-id-for-tests");
        pedido.setEnvio(envio);
        // Add required entity
        Cuenta cuenta;
        cuenta = CuentaResourceIT.createEntity();
        cuenta.setId("fixed-id-for-tests");
        pedido.setCuenta(cuenta);
        return pedido;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pedido createUpdatedEntity() {
        Pedido updatedPedido = new Pedido()
            .numeroPedido(UPDATED_NUMERO_PEDIDO)
            .estado(UPDATED_ESTADO)
            .subtotal(UPDATED_SUBTOTAL)
            .descuento(UPDATED_DESCUENTO)
            .ivaTotal(UPDATED_IVA_TOTAL)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .total(UPDATED_TOTAL)
            .notasCliente(UPDATED_NOTAS_CLIENTE)
            .notasInternas(UPDATED_NOTAS_INTERNAS)
            .ipOrigen(UPDATED_IP_ORIGEN)
            .userAgent(UPDATED_USER_AGENT);
        // Add required entity
        Direccion direccion;
        direccion = DireccionResourceIT.createUpdatedEntity();
        direccion.setId("fixed-id-for-tests");
        updatedPedido.setDireccion(direccion);
        // Add required entity
        Envio envio;
        envio = EnvioResourceIT.createUpdatedEntity();
        envio.setId("fixed-id-for-tests");
        updatedPedido.setEnvio(envio);
        // Add required entity
        Cuenta cuenta;
        cuenta = CuentaResourceIT.createUpdatedEntity();
        cuenta.setId("fixed-id-for-tests");
        updatedPedido.setCuenta(cuenta);
        return updatedPedido;
    }

    @BeforeEach
    void initTest() {
        pedido = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPedido != null) {
            pedidoRepository.delete(insertedPedido);
            insertedPedido = null;
        }
    }

    @Test
    void createPedido() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);
        var returnedPedidoDTO = om.readValue(
            restPedidoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PedidoDTO.class
        );

        // Validate the Pedido in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPedido = pedidoMapper.toEntity(returnedPedidoDTO);
        assertPedidoUpdatableFieldsEquals(returnedPedido, getPersistedPedido(returnedPedido));

        insertedPedido = returnedPedido;
    }

    @Test
    void createPedidoWithExistingId() throws Exception {
        // Create the Pedido with an existing ID
        pedido.setId("existing_id");
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNumeroPedidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pedido.setNumeroPedido(null);

        // Create the Pedido, which fails.
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pedido.setEstado(null);

        // Create the Pedido, which fails.
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pedido.setSubtotal(null);

        // Create the Pedido, which fails.
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pedido.setTotal(null);

        // Create the Pedido, which fails.
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPedidos() throws Exception {
        // Initialize the database
        insertedPedido = pedidoRepository.save(pedido);

        // Get all the pedidoList
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pedido.getId())))
            .andExpect(jsonPath("$.[*].numeroPedido").value(hasItem(DEFAULT_NUMERO_PEDIDO)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].descuento").value(hasItem(sameNumber(DEFAULT_DESCUENTO))))
            .andExpect(jsonPath("$.[*].ivaTotal").value(hasItem(sameNumber(DEFAULT_IVA_TOTAL))))
            .andExpect(jsonPath("$.[*].costoEnvio").value(hasItem(sameNumber(DEFAULT_COSTO_ENVIO))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].notasCliente").value(hasItem(DEFAULT_NOTAS_CLIENTE)))
            .andExpect(jsonPath("$.[*].notasInternas").value(hasItem(DEFAULT_NOTAS_INTERNAS)))
            .andExpect(jsonPath("$.[*].ipOrigen").value(hasItem(DEFAULT_IP_ORIGEN)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)));
    }

    @Test
    void getPedido() throws Exception {
        // Initialize the database
        insertedPedido = pedidoRepository.save(pedido);

        // Get the pedido
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL_ID, pedido.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pedido.getId()))
            .andExpect(jsonPath("$.numeroPedido").value(DEFAULT_NUMERO_PEDIDO))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.descuento").value(sameNumber(DEFAULT_DESCUENTO)))
            .andExpect(jsonPath("$.ivaTotal").value(sameNumber(DEFAULT_IVA_TOTAL)))
            .andExpect(jsonPath("$.costoEnvio").value(sameNumber(DEFAULT_COSTO_ENVIO)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.notasCliente").value(DEFAULT_NOTAS_CLIENTE))
            .andExpect(jsonPath("$.notasInternas").value(DEFAULT_NOTAS_INTERNAS))
            .andExpect(jsonPath("$.ipOrigen").value(DEFAULT_IP_ORIGEN))
            .andExpect(jsonPath("$.userAgent").value(DEFAULT_USER_AGENT));
    }

    @Test
    void getNonExistingPedido() throws Exception {
        // Get the pedido
        restPedidoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPedido() throws Exception {
        // Initialize the database
        insertedPedido = pedidoRepository.save(pedido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pedido
        Pedido updatedPedido = pedidoRepository.findById(pedido.getId()).orElseThrow();
        updatedPedido
            .numeroPedido(UPDATED_NUMERO_PEDIDO)
            .estado(UPDATED_ESTADO)
            .subtotal(UPDATED_SUBTOTAL)
            .descuento(UPDATED_DESCUENTO)
            .ivaTotal(UPDATED_IVA_TOTAL)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .total(UPDATED_TOTAL)
            .notasCliente(UPDATED_NOTAS_CLIENTE)
            .notasInternas(UPDATED_NOTAS_INTERNAS)
            .ipOrigen(UPDATED_IP_ORIGEN)
            .userAgent(UPDATED_USER_AGENT);
        PedidoDTO pedidoDTO = pedidoMapper.toDto(updatedPedido);

        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pedidoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPedidoToMatchAllProperties(updatedPedido);
    }

    @Test
    void putNonExistingPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pedido.setId(UUID.randomUUID().toString());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pedidoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pedido.setId(UUID.randomUUID().toString());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pedido.setId(UUID.randomUUID().toString());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pedidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePedidoWithPatch() throws Exception {
        // Initialize the database
        insertedPedido = pedidoRepository.save(pedido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pedido using partial update
        Pedido partialUpdatedPedido = new Pedido();
        partialUpdatedPedido.setId(pedido.getId());

        partialUpdatedPedido
            .subtotal(UPDATED_SUBTOTAL)
            .descuento(UPDATED_DESCUENTO)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .ipOrigen(UPDATED_IP_ORIGEN)
            .userAgent(UPDATED_USER_AGENT);

        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPedido))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPedidoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPedido, pedido), getPersistedPedido(pedido));
    }

    @Test
    void fullUpdatePedidoWithPatch() throws Exception {
        // Initialize the database
        insertedPedido = pedidoRepository.save(pedido);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pedido using partial update
        Pedido partialUpdatedPedido = new Pedido();
        partialUpdatedPedido.setId(pedido.getId());

        partialUpdatedPedido
            .numeroPedido(UPDATED_NUMERO_PEDIDO)
            .estado(UPDATED_ESTADO)
            .subtotal(UPDATED_SUBTOTAL)
            .descuento(UPDATED_DESCUENTO)
            .ivaTotal(UPDATED_IVA_TOTAL)
            .costoEnvio(UPDATED_COSTO_ENVIO)
            .total(UPDATED_TOTAL)
            .notasCliente(UPDATED_NOTAS_CLIENTE)
            .notasInternas(UPDATED_NOTAS_INTERNAS)
            .ipOrigen(UPDATED_IP_ORIGEN)
            .userAgent(UPDATED_USER_AGENT);

        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPedido))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPedidoUpdatableFieldsEquals(partialUpdatedPedido, getPersistedPedido(partialUpdatedPedido));
    }

    @Test
    void patchNonExistingPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pedido.setId(UUID.randomUUID().toString());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pedidoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pedido.setId(UUID.randomUUID().toString());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPedido() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pedido.setId(UUID.randomUUID().toString());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pedidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pedido in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePedido() throws Exception {
        // Initialize the database
        insertedPedido = pedidoRepository.save(pedido);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pedido
        restPedidoMockMvc
            .perform(delete(ENTITY_API_URL_ID, pedido.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pedidoRepository.count();
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

    protected Pedido getPersistedPedido(Pedido pedido) {
        return pedidoRepository.findById(pedido.getId()).orElseThrow();
    }

    protected void assertPersistedPedidoToMatchAllProperties(Pedido expectedPedido) {
        assertPedidoAllPropertiesEquals(expectedPedido, getPersistedPedido(expectedPedido));
    }

    protected void assertPersistedPedidoToMatchUpdatableProperties(Pedido expectedPedido) {
        assertPedidoAllUpdatablePropertiesEquals(expectedPedido, getPersistedPedido(expectedPedido));
    }
}
