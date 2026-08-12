package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.CuentaAsserts.*;
import static com.mycompany.knstore.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.knstore.IntegrationTest;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.TipoDocumento;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.domain.enumeration.Genero;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.TipoDocumentoRepository;
import com.mycompany.knstore.repository.UserRepository;
import com.mycompany.knstore.service.CuentaService;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.mapper.CuentaMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link CuentaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(roles = { "ADMIN", "MANAGER" })
class CuentaResourceIT {

    private static final String DEFAULT_NUM_DOCUMENTO = "1234567890";
    private static final String UPDATED_NUM_DOCUMENTO = "2234567890";

    private static final String DEFAULT_PRIMER_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_PRIMER_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_SEGUNDO_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_SEGUNDO_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMER_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_PRIMER_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_SEGUNDO_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_SEGUNDO_APELLIDO = "BBBBBBBBBB";

    private static final Genero DEFAULT_GENERO = Genero.MASCULINO;
    private static final Genero UPDATED_GENERO = Genero.FEMENINO;

    private static final LocalDate DEFAULT_FECHA_NACIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_NACIMIENTO = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_CELULAR = "1234567890";
    private static final String UPDATED_CELULAR = "2234567890";

    private static final String DEFAULT_TELEFONO = "1234567890";
    private static final String UPDATED_TELEFONO = "2234567890";

    private static final byte[] DEFAULT_FOTO_PERFIL = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FOTO_PERFIL = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FOTO_PERFIL_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FOTO_PERFIL_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final String ENTITY_API_URL = "/api/cuentas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CuentaRepository cuentaRepositoryMock;

    @Autowired
    private CuentaMapper cuentaMapper;

    @Mock
    private CuentaService cuentaServiceMock;

    @Autowired
    private MockMvc restCuentaMockMvc;

    private Cuenta cuenta;

    private Cuenta insertedCuenta;

    private Cuenta insertedOtherCuenta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cuenta createEntity() {
        Cuenta cuenta = new Cuenta()
            .numDocumento(DEFAULT_NUM_DOCUMENTO)
            .primerNombre(DEFAULT_PRIMER_NOMBRE)
            .segundoNombre(DEFAULT_SEGUNDO_NOMBRE)
            .primerApellido(DEFAULT_PRIMER_APELLIDO)
            .segundoApellido(DEFAULT_SEGUNDO_APELLIDO)
            .genero(DEFAULT_GENERO)
            .fechaNacimiento(DEFAULT_FECHA_NACIMIENTO)
            .celular(DEFAULT_CELULAR)
            .telefono(DEFAULT_TELEFONO)
            .fotoPerfil(DEFAULT_FOTO_PERFIL)
            .fotoPerfilContentType(DEFAULT_FOTO_PERFIL_CONTENT_TYPE)
            .activo(DEFAULT_ACTIVO);
        // Add required entity
        User user = UserResourceIT.createEntity();
        user.setId("fixed-id-for-tests");
        cuenta.setUser(user);
        TipoDocumento tipoDocumento = TipoDocumentoResourceIT.createEntity();
        tipoDocumento.setId("fixed-id-for-tests-tipo-documento");
        cuenta.setTipoDocumento(tipoDocumento);
        return cuenta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cuenta createUpdatedEntity() {
        Cuenta updatedCuenta = new Cuenta()
            .numDocumento(UPDATED_NUM_DOCUMENTO)
            .primerNombre(UPDATED_PRIMER_NOMBRE)
            .segundoNombre(UPDATED_SEGUNDO_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .genero(UPDATED_GENERO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .celular(UPDATED_CELULAR)
            .telefono(UPDATED_TELEFONO)
            .fotoPerfil(UPDATED_FOTO_PERFIL)
            .fotoPerfilContentType(UPDATED_FOTO_PERFIL_CONTENT_TYPE)
            .activo(UPDATED_ACTIVO);
        // Add required entity
        User user = UserResourceIT.createEntity();
        user.setId("fixed-id-for-tests");
        updatedCuenta.setUser(user);
        return updatedCuenta;
    }

    @BeforeEach
    void initTest() {
        cuenta = createEntity();
        // Persistir el tipo de documento referenciado para que el DBRef resuelva.
        tipoDocumentoRepository.save(cuenta.getTipoDocumento());
    }

    @AfterEach
    void cleanup() {
        if (insertedCuenta != null) {
            cuentaRepository.delete(insertedCuenta);
            insertedCuenta = null;
        }
        if (insertedOtherCuenta != null) {
            cuentaRepository.delete(insertedOtherCuenta);
            insertedOtherCuenta = null;
        }
        userRepository.deleteAll();
    }

    @Test
    void createCuenta() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);
        var returnedCuentaDTO = om.readValue(
            restCuentaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CuentaDTO.class
        );

        // Validate the Cuenta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCuenta = cuentaMapper.toEntity(returnedCuentaDTO);
        assertCuentaUpdatableFieldsEquals(returnedCuenta, getPersistedCuenta(returnedCuenta));

        insertedCuenta = returnedCuenta;
    }

    @Test
    void createCuentaWithExistingId() throws Exception {
        // Create the Cuenta with an existing ID
        cuenta.setId("existing_id");
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkPrimerNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cuenta.setPrimerNombre(null);

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrimerApellidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cuenta.setPrimerApellido(null);

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cuenta.setActivo(null);

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void createCuentaWithCelularConLetras() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field with letters
        cuenta.setCelular("12345ABC");

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void createCuentaWithPrimerNombreConDigitos() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field with digits
        cuenta.setPrimerNombre("Juan123");

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void createCuentaWithFechaNacimientoFutura() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field with a future date
        cuenta.setFechaNacimiento(LocalDate.now().plusDays(1));

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void createCuentaWithFechaNacimientoMayor100Anios() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field with a date older than 100 years
        cuenta.setFechaNacimiento(LocalDate.now().minusYears(101));

        // Create the Cuenta, which fails.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        restCuentaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void createCuentaWithFechaNacimientoLimiteInclusivo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // set the field with the inclusive lower bound (exactly 100 years ago)
        cuenta.setFechaNacimiento(LocalDate.now().minusYears(100));

        // Create the Cuenta, which succeeds.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);
        var returnedCuentaDTO = om.readValue(
            restCuentaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CuentaDTO.class
        );

        // Validate the Cuenta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);

        insertedCuenta = cuentaMapper.toEntity(returnedCuentaDTO);
    }

    @Test
    void createCuentaWithNombresConAcentosEsValido() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // set the fields with accents, apostrophe and spaces
        cuenta.setPrimerNombre("María del Pilar");
        cuenta.setPrimerApellido("O'Brian");

        // Create the Cuenta, which succeeds.
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);
        var returnedCuentaDTO = om.readValue(
            restCuentaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CuentaDTO.class
        );

        // Validate the Cuenta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);

        insertedCuenta = cuentaMapper.toEntity(returnedCuentaDTO);
    }

    @Test
    void partialUpdateCuentaWithSegundoNombreConDigitos() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        // Update the cuenta using partial update with a segundoNombre containing digits
        Cuenta partialUpdatedCuenta = new Cuenta();
        partialUpdatedCuenta.setId(cuenta.getId());

        partialUpdatedCuenta
            .numDocumento(cuenta.getNumDocumento())
            .primerNombre(cuenta.getPrimerNombre())
            .segundoNombre("Nombre2")
            .primerApellido(cuenta.getPrimerApellido())
            .segundoApellido(cuenta.getSegundoApellido())
            .genero(cuenta.getGenero())
            .fechaNacimiento(cuenta.getFechaNacimiento())
            .celular(cuenta.getCelular())
            .telefono(cuenta.getTelefono())
            .activo(cuenta.getActivo());
        partialUpdatedCuenta.setUser(cuenta.getUser());
        partialUpdatedCuenta.setTipoDocumento(cuenta.getTipoDocumento());

        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuenta))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void putCuentaWithCelularConLetras() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        // Update the cuenta with a celular containing letters
        Cuenta updatedCuenta = cuentaRepository.findById(cuenta.getId()).orElseThrow();
        updatedCuenta.setCelular("12345ABC");
        CuentaDTO cuentaDTO = cuentaMapper.toDto(updatedCuenta);

        restCuentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cuentaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void partialUpdateCuentaWithCelularConLetras() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        // Update the cuenta using partial update with a celular containing letters
        Cuenta partialUpdatedCuenta = new Cuenta();
        partialUpdatedCuenta.setId(cuenta.getId());

        partialUpdatedCuenta
            .numDocumento(cuenta.getNumDocumento())
            .primerNombre(cuenta.getPrimerNombre())
            .segundoNombre(cuenta.getSegundoNombre())
            .primerApellido(cuenta.getPrimerApellido())
            .segundoApellido(cuenta.getSegundoApellido())
            .genero(cuenta.getGenero())
            .fechaNacimiento(cuenta.getFechaNacimiento())
            .celular("12345ABC")
            .telefono(cuenta.getTelefono())
            .activo(cuenta.getActivo());
        partialUpdatedCuenta.setUser(cuenta.getUser());
        partialUpdatedCuenta.setTipoDocumento(cuenta.getTipoDocumento());

        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuenta))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void partialUpdateCuentaWithDocumentoDuplicado() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        // A second cuenta with a different document number for the same tipo documento
        Cuenta otherCuenta = createEntity();
        otherCuenta.setId(UUID.randomUUID().toString());
        otherCuenta.setNumDocumento("9999999999");
        otherCuenta.setUser(cuenta.getUser());
        otherCuenta.setTipoDocumento(cuenta.getTipoDocumento());
        insertedOtherCuenta = cuentaRepository.save(otherCuenta);

        // PATCH the second cuenta to reuse the document number of the first one
        CuentaDTO otherDTO = cuentaMapper.toDto(otherCuenta);
        otherDTO.setNumDocumento(cuenta.getNumDocumento());

        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, otherCuenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(otherDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.documentoduplicado"));
    }

    @Test
    void getAllCuentas() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        // Get all the cuentaList
        restCuentaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cuenta.getId())))
            .andExpect(jsonPath("$.[*].numDocumento").value(hasItem(DEFAULT_NUM_DOCUMENTO)))
            .andExpect(jsonPath("$.[*].primerNombre").value(hasItem(DEFAULT_PRIMER_NOMBRE)))
            .andExpect(jsonPath("$.[*].segundoNombre").value(hasItem(DEFAULT_SEGUNDO_NOMBRE)))
            .andExpect(jsonPath("$.[*].primerApellido").value(hasItem(DEFAULT_PRIMER_APELLIDO)))
            .andExpect(jsonPath("$.[*].segundoApellido").value(hasItem(DEFAULT_SEGUNDO_APELLIDO)))
            .andExpect(jsonPath("$.[*].genero").value(hasItem(DEFAULT_GENERO.toString())))
            .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())))
            .andExpect(jsonPath("$.[*].celular").value(hasItem(DEFAULT_CELULAR)))
            .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO)))
            .andExpect(jsonPath("$.[*].fotoPerfilContentType").value(hasItem(DEFAULT_FOTO_PERFIL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fotoPerfil").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FOTO_PERFIL))))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCuentasWithEagerRelationshipsIsEnabled() throws Exception {
        when(cuentaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCuentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(cuentaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCuentasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(cuentaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCuentaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(cuentaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getCuenta() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        // Get the cuenta
        restCuentaMockMvc
            .perform(get(ENTITY_API_URL_ID, cuenta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cuenta.getId()))
            .andExpect(jsonPath("$.numDocumento").value(DEFAULT_NUM_DOCUMENTO))
            .andExpect(jsonPath("$.primerNombre").value(DEFAULT_PRIMER_NOMBRE))
            .andExpect(jsonPath("$.segundoNombre").value(DEFAULT_SEGUNDO_NOMBRE))
            .andExpect(jsonPath("$.primerApellido").value(DEFAULT_PRIMER_APELLIDO))
            .andExpect(jsonPath("$.segundoApellido").value(DEFAULT_SEGUNDO_APELLIDO))
            .andExpect(jsonPath("$.genero").value(DEFAULT_GENERO.toString()))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO.toString()))
            .andExpect(jsonPath("$.celular").value(DEFAULT_CELULAR))
            .andExpect(jsonPath("$.telefono").value(DEFAULT_TELEFONO))
            .andExpect(jsonPath("$.fotoPerfilContentType").value(DEFAULT_FOTO_PERFIL_CONTENT_TYPE))
            .andExpect(jsonPath("$.fotoPerfil").value(Base64.getEncoder().encodeToString(DEFAULT_FOTO_PERFIL)))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO));
    }

    @Test
    void getNonExistingCuenta() throws Exception {
        // Get the cuenta
        restCuentaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingCuenta() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cuenta
        Cuenta updatedCuenta = cuentaRepository.findById(cuenta.getId()).orElseThrow();
        updatedCuenta
            .numDocumento(UPDATED_NUM_DOCUMENTO)
            .primerNombre(UPDATED_PRIMER_NOMBRE)
            .segundoNombre(UPDATED_SEGUNDO_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .genero(UPDATED_GENERO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .celular(UPDATED_CELULAR)
            .telefono(UPDATED_TELEFONO)
            .fotoPerfil(UPDATED_FOTO_PERFIL)
            .fotoPerfilContentType(UPDATED_FOTO_PERFIL_CONTENT_TYPE)
            .activo(UPDATED_ACTIVO);
        CuentaDTO cuentaDTO = cuentaMapper.toDto(updatedCuenta);
        if (cuentaDTO.getUser() == null) {
            com.mycompany.knstore.service.dto.UserDTO relDto = new com.mycompany.knstore.service.dto.UserDTO();
            relDto.setId(insertedCuenta.getUser().getId());
            cuentaDTO.setUser(relDto);
        }

        restCuentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cuentaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCuentaToMatchAllProperties(updatedCuenta);
    }

    @Test
    void putNonExistingCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(UUID.randomUUID().toString());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cuentaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(UUID.randomUUID().toString());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(UUID.randomUUID().toString());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCuentaWithPatch() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cuenta using partial update
        Cuenta partialUpdatedCuenta = new Cuenta();
        partialUpdatedCuenta.setId(cuenta.getId());

        partialUpdatedCuenta
            .numDocumento(cuenta.getNumDocumento())
            .primerNombre(cuenta.getPrimerNombre())
            .segundoNombre(cuenta.getSegundoNombre())
            .primerApellido(cuenta.getPrimerApellido())
            .segundoApellido(cuenta.getSegundoApellido())
            .genero(UPDATED_GENERO)
            .fechaNacimiento(cuenta.getFechaNacimiento())
            .celular(cuenta.getCelular())
            .telefono(cuenta.getTelefono())
            .fotoPerfil(UPDATED_FOTO_PERFIL)
            .fotoPerfilContentType(UPDATED_FOTO_PERFIL_CONTENT_TYPE)
            .activo(UPDATED_ACTIVO);
        partialUpdatedCuenta.setUser(cuenta.getUser());
        partialUpdatedCuenta.setTipoDocumento(cuenta.getTipoDocumento());

        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuenta))
            )
            .andExpect(status().isOk());

        // Validate the Cuenta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCuentaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCuenta, cuenta), getPersistedCuenta(cuenta));
    }

    @Test
    void fullUpdateCuentaWithPatch() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cuenta using partial update
        Cuenta partialUpdatedCuenta = new Cuenta();
        partialUpdatedCuenta.setId(cuenta.getId());

        partialUpdatedCuenta
            .numDocumento(UPDATED_NUM_DOCUMENTO)
            .primerNombre(UPDATED_PRIMER_NOMBRE)
            .segundoNombre(UPDATED_SEGUNDO_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .genero(UPDATED_GENERO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .celular(UPDATED_CELULAR)
            .telefono(UPDATED_TELEFONO)
            .fotoPerfil(UPDATED_FOTO_PERFIL)
            .fotoPerfilContentType(UPDATED_FOTO_PERFIL_CONTENT_TYPE)
            .activo(UPDATED_ACTIVO);
        partialUpdatedCuenta.setUser(cuenta.getUser());
        partialUpdatedCuenta.setTipoDocumento(cuenta.getTipoDocumento());

        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuenta.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuenta))
            )
            .andExpect(status().isOk());

        // Validate the Cuenta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCuentaUpdatableFieldsEquals(partialUpdatedCuenta, getPersistedCuenta(partialUpdatedCuenta));
    }

    @Test
    void patchNonExistingCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(UUID.randomUUID().toString());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cuentaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(UUID.randomUUID().toString());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cuentaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCuenta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cuenta.setId(UUID.randomUUID().toString());

        // Create the Cuenta
        CuentaDTO cuentaDTO = cuentaMapper.toDto(cuenta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuentaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cuentaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cuenta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCuenta() throws Exception {
        // Initialize the database
        insertedCuenta = cuentaRepository.save(cuenta);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cuenta
        restCuentaMockMvc
            .perform(delete(ENTITY_API_URL_ID, cuenta.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cuentaRepository.count();
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

    protected Cuenta getPersistedCuenta(Cuenta cuenta) {
        return cuentaRepository.findById(cuenta.getId()).orElseThrow();
    }

    protected void assertPersistedCuentaToMatchAllProperties(Cuenta expectedCuenta) {
        assertCuentaAllPropertiesEquals(expectedCuenta, getPersistedCuenta(expectedCuenta));
    }

    protected void assertPersistedCuentaToMatchUpdatableProperties(Cuenta expectedCuenta) {
        assertCuentaAllUpdatablePropertiesEquals(expectedCuenta, getPersistedCuenta(expectedCuenta));
    }
}
