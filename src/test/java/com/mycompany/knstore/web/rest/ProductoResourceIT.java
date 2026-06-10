package com.mycompany.knstore.web.rest;

import static com.mycompany.knstore.domain.ProductoAsserts.*;
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
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.domain.enumeration.CategoriaIVA;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.service.ProductoService;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ProductoMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ProductoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductoResourceIT {

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

    private static final String DEFAULT_IMAGEN_ALT = "AAAAAAAAAA";
    private static final String UPDATED_IMAGEN_ALT = "BBBBBBBBBB";

    private static final String DEFAULT_MARCA = "AAAAAAAAAA";
    private static final String UPDATED_MARCA = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCIA = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCIA = "BBBBBBBBBB";

    private static final String DEFAULT_CODIGO_BARRAS = "AAAAAAAAAA";
    private static final String UPDATED_CODIGO_BARRAS = "BBBBBBBBBB";

    private static final String DEFAULT_UNIDAD_MEDIDA = "AAAAAAAAAA";
    private static final String UPDATED_UNIDAD_MEDIDA = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PESO_KG = new BigDecimal(0);
    private static final BigDecimal UPDATED_PESO_KG = new BigDecimal(1);

    private static final BigDecimal DEFAULT_LARGO_CM = new BigDecimal(0);
    private static final BigDecimal UPDATED_LARGO_CM = new BigDecimal(1);

    private static final BigDecimal DEFAULT_ANCHO_CM = new BigDecimal(0);
    private static final BigDecimal UPDATED_ANCHO_CM = new BigDecimal(1);

    private static final BigDecimal DEFAULT_ALTO_CM = new BigDecimal(0);
    private static final BigDecimal UPDATED_ALTO_CM = new BigDecimal(1);

    private static final CategoriaIVA DEFAULT_CATEGORIA_IVA = CategoriaIVA.EXCLUIDO;
    private static final CategoriaIVA UPDATED_CATEGORIA_IVA = CategoriaIVA.EXENTO;

    private static final BigDecimal DEFAULT_PRECIO_COMPRA = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_COMPRA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_PRECIO_VENTA = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_VENTA = new BigDecimal(1);

    private static final BigDecimal DEFAULT_GANANCIA = new BigDecimal(1);
    private static final BigDecimal UPDATED_GANANCIA = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MARGEN = new BigDecimal(1);
    private static final BigDecimal UPDATED_MARGEN = new BigDecimal(2);

    private static final Integer DEFAULT_GARANTIA_MESES = 0;
    private static final Integer UPDATED_GARANTIA_MESES = 1;

    private static final Boolean DEFAULT_DESTACADO = false;
    private static final Boolean UPDATED_DESTACADO = true;

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final String ENTITY_API_URL = "/api/productos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductoRepository productoRepository;

    @Mock
    private ProductoRepository productoRepositoryMock;

    @Autowired
    private ProductoMapper productoMapper;

    @Mock
    private ProductoService productoServiceMock;

    @Autowired
    private MockMvc restProductoMockMvc;

    private Producto producto;

    private Producto insertedProducto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createEntity() {
        Producto producto = new Producto()
            .nombre(DEFAULT_NOMBRE)
            .slug(DEFAULT_SLUG)
            .descripcion(DEFAULT_DESCRIPCION)
            .imagen(DEFAULT_IMAGEN)
            .imagenContentType(DEFAULT_IMAGEN_CONTENT_TYPE)
            .imagenAlt(DEFAULT_IMAGEN_ALT)
            .marca(DEFAULT_MARCA)
            .referencia(DEFAULT_REFERENCIA)
            .codigoBarras(DEFAULT_CODIGO_BARRAS)
            .unidadMedida(DEFAULT_UNIDAD_MEDIDA)
            .pesoKg(DEFAULT_PESO_KG)
            .largoCm(DEFAULT_LARGO_CM)
            .anchoCm(DEFAULT_ANCHO_CM)
            .altoCm(DEFAULT_ALTO_CM)
            .categoriaIva(DEFAULT_CATEGORIA_IVA)
            .precioCompra(DEFAULT_PRECIO_COMPRA)
            .precioVenta(DEFAULT_PRECIO_VENTA)
            .ganancia(DEFAULT_GANANCIA)
            .margen(DEFAULT_MARGEN)
            .garantiaMeses(DEFAULT_GARANTIA_MESES)
            .destacado(DEFAULT_DESTACADO)
            .activo(DEFAULT_ACTIVO);
        // Add required entity
        Subcategoria subcategoria;
        subcategoria = SubcategoriaResourceIT.createEntity();
        subcategoria.setId("fixed-id-for-tests");
        producto.setSubcategoria(subcategoria);
        return producto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createUpdatedEntity() {
        Producto updatedProducto = new Producto()
            .nombre(UPDATED_NOMBRE)
            .slug(UPDATED_SLUG)
            .descripcion(UPDATED_DESCRIPCION)
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .imagenAlt(UPDATED_IMAGEN_ALT)
            .marca(UPDATED_MARCA)
            .referencia(UPDATED_REFERENCIA)
            .codigoBarras(UPDATED_CODIGO_BARRAS)
            .unidadMedida(UPDATED_UNIDAD_MEDIDA)
            .pesoKg(UPDATED_PESO_KG)
            .largoCm(UPDATED_LARGO_CM)
            .anchoCm(UPDATED_ANCHO_CM)
            .altoCm(UPDATED_ALTO_CM)
            .categoriaIva(UPDATED_CATEGORIA_IVA)
            .precioCompra(UPDATED_PRECIO_COMPRA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .ganancia(UPDATED_GANANCIA)
            .margen(UPDATED_MARGEN)
            .garantiaMeses(UPDATED_GARANTIA_MESES)
            .destacado(UPDATED_DESTACADO)
            .activo(UPDATED_ACTIVO);
        // Add required entity
        Subcategoria subcategoria;
        subcategoria = SubcategoriaResourceIT.createUpdatedEntity();
        subcategoria.setId("fixed-id-for-tests");
        updatedProducto.setSubcategoria(subcategoria);
        return updatedProducto;
    }

    @BeforeEach
    void initTest() {
        producto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProducto != null) {
            productoRepository.delete(insertedProducto);
            insertedProducto = null;
        }
    }

    @Test
    void createProducto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);
        var returnedProductoDTO = om.readValue(
            restProductoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductoDTO.class
        );

        // Validate the Producto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProducto = productoMapper.toEntity(returnedProductoDTO);
        assertProductoUpdatableFieldsEquals(returnedProducto, getPersistedProducto(returnedProducto));

        insertedProducto = returnedProducto;
    }

    @Test
    void createProductoWithExistingId() throws Exception {
        // Create the Producto with an existing ID
        producto.setId("existing_id");
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setNombre(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setSlug(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCategoriaIvaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setCategoriaIva(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioCompraIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setPrecioCompra(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioVentaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setPrecioVenta(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDestacadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setDestacado(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        producto.setActivo(null);

        // Create the Producto, which fails.
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProductos() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.save(producto);

        // Get all the productoList
        restProductoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(producto.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].imagenContentType").value(hasItem(DEFAULT_IMAGEN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imagen").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGEN))))
            .andExpect(jsonPath("$.[*].imagenAlt").value(hasItem(DEFAULT_IMAGEN_ALT)))
            .andExpect(jsonPath("$.[*].marca").value(hasItem(DEFAULT_MARCA)))
            .andExpect(jsonPath("$.[*].referencia").value(hasItem(DEFAULT_REFERENCIA)))
            .andExpect(jsonPath("$.[*].codigoBarras").value(hasItem(DEFAULT_CODIGO_BARRAS)))
            .andExpect(jsonPath("$.[*].unidadMedida").value(hasItem(DEFAULT_UNIDAD_MEDIDA)))
            .andExpect(jsonPath("$.[*].pesoKg").value(hasItem(sameNumber(DEFAULT_PESO_KG))))
            .andExpect(jsonPath("$.[*].largoCm").value(hasItem(sameNumber(DEFAULT_LARGO_CM))))
            .andExpect(jsonPath("$.[*].anchoCm").value(hasItem(sameNumber(DEFAULT_ANCHO_CM))))
            .andExpect(jsonPath("$.[*].altoCm").value(hasItem(sameNumber(DEFAULT_ALTO_CM))))
            .andExpect(jsonPath("$.[*].categoriaIva").value(hasItem(DEFAULT_CATEGORIA_IVA.toString())))
            .andExpect(jsonPath("$.[*].precioCompra").value(hasItem(sameNumber(DEFAULT_PRECIO_COMPRA))))
            .andExpect(jsonPath("$.[*].precioVenta").value(hasItem(sameNumber(DEFAULT_PRECIO_VENTA))))
            .andExpect(jsonPath("$.[*].ganancia").value(hasItem(sameNumber(DEFAULT_GANANCIA))))
            .andExpect(jsonPath("$.[*].margen").value(hasItem(sameNumber(DEFAULT_MARGEN))))
            .andExpect(jsonPath("$.[*].garantiaMeses").value(hasItem(DEFAULT_GARANTIA_MESES)))
            .andExpect(jsonPath("$.[*].destacado").value(hasItem(DEFAULT_DESTACADO)))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductosWithEagerRelationshipsIsEnabled() throws Exception {
        when(productoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.save(producto);

        // Get the producto
        restProductoMockMvc
            .perform(get(ENTITY_API_URL_ID, producto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(producto.getId()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.imagenContentType").value(DEFAULT_IMAGEN_CONTENT_TYPE))
            .andExpect(jsonPath("$.imagen").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGEN)))
            .andExpect(jsonPath("$.imagenAlt").value(DEFAULT_IMAGEN_ALT))
            .andExpect(jsonPath("$.marca").value(DEFAULT_MARCA))
            .andExpect(jsonPath("$.referencia").value(DEFAULT_REFERENCIA))
            .andExpect(jsonPath("$.codigoBarras").value(DEFAULT_CODIGO_BARRAS))
            .andExpect(jsonPath("$.unidadMedida").value(DEFAULT_UNIDAD_MEDIDA))
            .andExpect(jsonPath("$.pesoKg").value(sameNumber(DEFAULT_PESO_KG)))
            .andExpect(jsonPath("$.largoCm").value(sameNumber(DEFAULT_LARGO_CM)))
            .andExpect(jsonPath("$.anchoCm").value(sameNumber(DEFAULT_ANCHO_CM)))
            .andExpect(jsonPath("$.altoCm").value(sameNumber(DEFAULT_ALTO_CM)))
            .andExpect(jsonPath("$.categoriaIva").value(DEFAULT_CATEGORIA_IVA.toString()))
            .andExpect(jsonPath("$.precioCompra").value(sameNumber(DEFAULT_PRECIO_COMPRA)))
            .andExpect(jsonPath("$.precioVenta").value(sameNumber(DEFAULT_PRECIO_VENTA)))
            .andExpect(jsonPath("$.ganancia").value(sameNumber(DEFAULT_GANANCIA)))
            .andExpect(jsonPath("$.margen").value(sameNumber(DEFAULT_MARGEN)))
            .andExpect(jsonPath("$.garantiaMeses").value(DEFAULT_GARANTIA_MESES))
            .andExpect(jsonPath("$.destacado").value(DEFAULT_DESTACADO))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO));
    }

    @Test
    void getNonExistingProducto() throws Exception {
        // Get the producto
        restProductoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.save(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto
        Producto updatedProducto = productoRepository.findById(producto.getId()).orElseThrow();
        updatedProducto
            .nombre(UPDATED_NOMBRE)
            .slug(UPDATED_SLUG)
            .descripcion(UPDATED_DESCRIPCION)
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .imagenAlt(UPDATED_IMAGEN_ALT)
            .marca(UPDATED_MARCA)
            .referencia(UPDATED_REFERENCIA)
            .codigoBarras(UPDATED_CODIGO_BARRAS)
            .unidadMedida(UPDATED_UNIDAD_MEDIDA)
            .pesoKg(UPDATED_PESO_KG)
            .largoCm(UPDATED_LARGO_CM)
            .anchoCm(UPDATED_ANCHO_CM)
            .altoCm(UPDATED_ALTO_CM)
            .categoriaIva(UPDATED_CATEGORIA_IVA)
            .precioCompra(UPDATED_PRECIO_COMPRA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .ganancia(UPDATED_GANANCIA)
            .margen(UPDATED_MARGEN)
            .garantiaMeses(UPDATED_GARANTIA_MESES)
            .destacado(UPDATED_DESTACADO)
            .activo(UPDATED_ACTIVO);
        ProductoDTO productoDTO = productoMapper.toDto(updatedProducto);

        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductoToMatchAllProperties(updatedProducto);
    }

    @Test
    void putNonExistingProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(UUID.randomUUID().toString());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(UUID.randomUUID().toString());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(UUID.randomUUID().toString());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.save(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto
            .slug(UPDATED_SLUG)
            .descripcion(UPDATED_DESCRIPCION)
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .imagenAlt(UPDATED_IMAGEN_ALT)
            .marca(UPDATED_MARCA)
            .referencia(UPDATED_REFERENCIA)
            .codigoBarras(UPDATED_CODIGO_BARRAS)
            .unidadMedida(UPDATED_UNIDAD_MEDIDA)
            .pesoKg(UPDATED_PESO_KG)
            .altoCm(UPDATED_ALTO_CM)
            .precioCompra(UPDATED_PRECIO_COMPRA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .ganancia(UPDATED_GANANCIA)
            .garantiaMeses(UPDATED_GARANTIA_MESES)
            .destacado(UPDATED_DESTACADO)
            .activo(UPDATED_ACTIVO);

        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducto))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProducto, producto), getPersistedProducto(producto));
    }

    @Test
    void fullUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.save(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto
            .nombre(UPDATED_NOMBRE)
            .slug(UPDATED_SLUG)
            .descripcion(UPDATED_DESCRIPCION)
            .imagen(UPDATED_IMAGEN)
            .imagenContentType(UPDATED_IMAGEN_CONTENT_TYPE)
            .imagenAlt(UPDATED_IMAGEN_ALT)
            .marca(UPDATED_MARCA)
            .referencia(UPDATED_REFERENCIA)
            .codigoBarras(UPDATED_CODIGO_BARRAS)
            .unidadMedida(UPDATED_UNIDAD_MEDIDA)
            .pesoKg(UPDATED_PESO_KG)
            .largoCm(UPDATED_LARGO_CM)
            .anchoCm(UPDATED_ANCHO_CM)
            .altoCm(UPDATED_ALTO_CM)
            .categoriaIva(UPDATED_CATEGORIA_IVA)
            .precioCompra(UPDATED_PRECIO_COMPRA)
            .precioVenta(UPDATED_PRECIO_VENTA)
            .ganancia(UPDATED_GANANCIA)
            .margen(UPDATED_MARGEN)
            .garantiaMeses(UPDATED_GARANTIA_MESES)
            .destacado(UPDATED_DESTACADO)
            .activo(UPDATED_ACTIVO);

        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducto))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoUpdatableFieldsEquals(partialUpdatedProducto, getPersistedProducto(partialUpdatedProducto));
    }

    @Test
    void patchNonExistingProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(UUID.randomUUID().toString());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(UUID.randomUUID().toString());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(UUID.randomUUID().toString());

        // Create the Producto
        ProductoDTO productoDTO = productoMapper.toDto(producto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.save(producto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the producto
        restProductoMockMvc
            .perform(delete(ENTITY_API_URL_ID, producto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productoRepository.count();
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

    protected Producto getPersistedProducto(Producto producto) {
        return productoRepository.findById(producto.getId()).orElseThrow();
    }

    protected void assertPersistedProductoToMatchAllProperties(Producto expectedProducto) {
        assertProductoAllPropertiesEquals(expectedProducto, getPersistedProducto(expectedProducto));
    }

    protected void assertPersistedProductoToMatchUpdatableProperties(Producto expectedProducto) {
        assertProductoAllUpdatablePropertiesEquals(expectedProducto, getPersistedProducto(expectedProducto));
    }
}
