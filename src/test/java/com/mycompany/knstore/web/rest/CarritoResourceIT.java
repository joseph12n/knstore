package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.CarritoAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.knstore.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.mapper.CarritoMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link CarritoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarritoResourceIT {

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);

    private static final Instant DEFAULT_FECHA_ACTUALIZACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_ACTUALIZACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/carritos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoMapper carritoMapper;

    @Autowired
    private MockMvc restCarritoMockMvc;

    private Carrito carrito;

    private Carrito insertedCarrito;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carrito createEntity() {
        Carrito carrito = new Carrito().subtotal(DEFAULT_SUBTOTAL).fechaActualizacion(DEFAULT_FECHA_ACTUALIZACION);
        // Add required entity
        Cuenta cuenta;
        cuenta = CuentaResourceIT.createEntity();
        cuenta.setId("fixed-id-for-tests");
        carrito.setCuenta(cuenta);
        return carrito;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carrito createUpdatedEntity() {
        Carrito updatedCarrito = new Carrito().subtotal(UPDATED_SUBTOTAL).fechaActualizacion(UPDATED_FECHA_ACTUALIZACION);
        // Add required entity
        Cuenta cuenta;
        cuenta = CuentaResourceIT.createUpdatedEntity();
        cuenta.setId("fixed-id-for-tests");
        updatedCarrito.setCuenta(cuenta);
        return updatedCarrito;
    }

    @BeforeEach
    void initTest() {
        carrito = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCarrito != null) {
            carritoRepository.delete(insertedCarrito);
            insertedCarrito = null;
        }
    }

    @Test
    void createCarrito() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);
        var returnedCarritoDTO = om.readValue(
            restCarritoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carritoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarritoDTO.class
        );

        // Validate the Carrito in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarrito = carritoMapper.toEntity(returnedCarritoDTO);
        assertCarritoUpdatableFieldsEquals(returnedCarrito, getPersistedCarrito(returnedCarrito));

        insertedCarrito = returnedCarrito;
    }

    @Test
    void createCarritoWithExistingId() throws Exception {
        // Create the Carrito with an existing ID
        carrito.setId("existing_id");
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarritoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carritoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCarritos() throws Exception {
        // Initialize the database
        insertedCarrito = carritoRepository.save(carrito);

        // Get all the carritoList
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carrito.getId())))
            .andExpect(jsonPath("$.[*].subtotal").value(hasItem(sameNumber(DEFAULT_SUBTOTAL))))
            .andExpect(jsonPath("$.[*].fechaActualizacion").value(hasItem(DEFAULT_FECHA_ACTUALIZACION.toString())));
    }

    @Test
    void getCarrito() throws Exception {
        // Initialize the database
        insertedCarrito = carritoRepository.save(carrito);

        // Get the carrito
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL_ID, carrito.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carrito.getId()))
            .andExpect(jsonPath("$.subtotal").value(sameNumber(DEFAULT_SUBTOTAL)))
            .andExpect(jsonPath("$.fechaActualizacion").value(DEFAULT_FECHA_ACTUALIZACION.toString()));
    }

    @Test
    void getNonExistingCarrito() throws Exception {
        // Get the carrito
        restCarritoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingCarrito() throws Exception {
        // Initialize the database
        insertedCarrito = carritoRepository.save(carrito);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carrito
        Carrito updatedCarrito = carritoRepository.findById(carrito.getId()).orElseThrow();
        updatedCarrito.subtotal(UPDATED_SUBTOTAL).fechaActualizacion(UPDATED_FECHA_ACTUALIZACION);
        CarritoDTO carritoDTO = carritoMapper.toDto(updatedCarrito);

        restCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carritoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carritoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarritoToMatchAllProperties(updatedCarrito);
    }

    @Test
    void putNonExistingCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carrito.setId(UUID.randomUUID().toString());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carritoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carrito.setId(UUID.randomUUID().toString());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carrito.setId(UUID.randomUUID().toString());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carritoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCarritoWithPatch() throws Exception {
        // Initialize the database
        insertedCarrito = carritoRepository.save(carrito);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carrito using partial update
        Carrito partialUpdatedCarrito = new Carrito();
        partialUpdatedCarrito.setId(carrito.getId());

        partialUpdatedCarrito.subtotal(UPDATED_SUBTOTAL);

        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarrito.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarrito))
            )
            .andExpect(status().isOk());

        // Validate the Carrito in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarritoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCarrito, carrito), getPersistedCarrito(carrito));
    }

    @Test
    void fullUpdateCarritoWithPatch() throws Exception {
        // Initialize the database
        insertedCarrito = carritoRepository.save(carrito);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carrito using partial update
        Carrito partialUpdatedCarrito = new Carrito();
        partialUpdatedCarrito.setId(carrito.getId());

        partialUpdatedCarrito.subtotal(UPDATED_SUBTOTAL).fechaActualizacion(UPDATED_FECHA_ACTUALIZACION);

        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarrito.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarrito))
            )
            .andExpect(status().isOk());

        // Validate the Carrito in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarritoUpdatableFieldsEquals(partialUpdatedCarrito, getPersistedCarrito(partialUpdatedCarrito));
    }

    @Test
    void patchNonExistingCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carrito.setId(UUID.randomUUID().toString());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carritoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carrito.setId(UUID.randomUUID().toString());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCarrito() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carrito.setId(UUID.randomUUID().toString());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carritoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carrito in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCarrito() throws Exception {
        // Initialize the database
        insertedCarrito = carritoRepository.save(carrito);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the carrito
        restCarritoMockMvc
            .perform(delete(ENTITY_API_URL_ID, carrito.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return carritoRepository.count();
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

    protected Carrito getPersistedCarrito(Carrito carrito) {
        return carritoRepository.findById(carrito.getId()).orElseThrow();
    }

    protected void assertPersistedCarritoToMatchAllProperties(Carrito expectedCarrito) {
        assertCarritoAllPropertiesEquals(expectedCarrito, getPersistedCarrito(expectedCarrito));
    }

    protected void assertPersistedCarritoToMatchUpdatableProperties(Carrito expectedCarrito) {
        assertCarritoAllUpdatablePropertiesEquals(expectedCarrito, getPersistedCarrito(expectedCarrito));
    }
}
