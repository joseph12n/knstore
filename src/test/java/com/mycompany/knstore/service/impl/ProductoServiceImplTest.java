package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoImagen;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.repository.CategoriaIVARepository;
import com.mycompany.knstore.repository.CategoriaRepository;
import com.mycompany.knstore.repository.MarcaRepository;
import com.mycompany.knstore.repository.ProductoImagenRepository;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.repository.SubcategoriaRepository;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ProductoMapperImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    private static final String PRODUCTO_ID_1 = "64b7f0c2a1b2c3d4e5f60001";
    private static final String PRODUCTO_ID_2 = "64b7f0c2a1b2c3d4e5f60002";

    private final com.mycompany.knstore.service.mapper.ProductoMapper productoMapper = new ProductoMapperImpl();

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoImagenRepository productoImagenRepository;

    @Mock
    private ProductoPrecioRepository productoPrecioRepository;

    @Mock
    private ProductoInventarioRepository productoInventarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private SubcategoriaRepository subcategoriaRepository;

    @Mock
    private MarcaRepository marcaRepository;

    @Mock
    private CategoriaIVARepository categoriaIVARepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private ProductoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductoServiceImpl(
            productoRepository,
            productoImagenRepository,
            productoPrecioRepository,
            productoInventarioRepository,
            categoriaRepository,
            subcategoriaRepository,
            marcaRepository,
            categoriaIVARepository,
            productoMapper,
            mongoTemplate
        );
    }

    @Test
    void findAllResuelveRelacionesEnLoteSinConsultasIndividuales() {
        Producto p1 = productoConRefs(PRODUCTO_ID_1, "pre-1", "inv-1", "cat-1", "sub-1", "m-1", "iva-1");
        Producto p2 = productoConRefs(PRODUCTO_ID_2, "pre-2", "inv-2", "cat-2", "sub-2", "m-2", "iva-2");
        when(productoRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(p1, p2), PageRequest.of(0, 10), 2));

        // entidades resueltas a partir de los findByIdIn
        when(productoPrecioRepository.findByIdIn(anyCollection())).thenReturn(
            List.of(precioResuelto("pre-1", "9900.99"), precioResuelto("pre-2", "12500.5"))
        );
        when(productoInventarioRepository.findByIdIn(anyCollection())).thenReturn(
            List.of(inventarioResuelto("inv-1", 20), inventarioResuelto("inv-2", 8))
        );
        when(categoriaRepository.findByIdIn(anyCollection())).thenReturn(
            List.of(categoriaResuelta("cat-1", "Categoria 1"), categoriaResuelta("cat-2", "Categoria 2"))
        );
        when(subcategoriaRepository.findByIdIn(anyCollection())).thenReturn(
            List.of(subcategoriaResuelta("sub-1", "Subcategoria 1"), subcategoriaResuelta("sub-2", "Subcategoria 2"))
        );
        when(marcaRepository.findByIdIn(anyCollection())).thenReturn(
            List.of(marcaResuelta("m-1", "Marca 1"), marcaResuelta("m-2", "Marca 2"))
        );
        when(categoriaIVARepository.findByIdIn(anyCollection())).thenReturn(
            List.of(catIvaResuelta("iva-1", "IVA 1"), catIvaResuelta("iva-2", "IVA 2"))
        );
        when(productoImagenRepository.findByProductoIdIn(anyCollection())).thenReturn(List.of());

        Page<ProductoDTO> page = service.findAll(PageRequest.of(0, 10));

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(2);

        ProductoDTO dto1 = buscarPorId(page.getContent(), PRODUCTO_ID_1);
        assertThat(dto1.getPrecio().getPrecioVenta()).isEqualByComparingTo(new BigDecimal("9900.99"));
        assertThat(dto1.getInventario().getStock()).isEqualTo(20);
        assertThat(dto1.getCategoria().getNombre()).isEqualTo("Categoria 1");
        assertThat(dto1.getSubcategoria().getNombre()).isEqualTo("Subcategoria 1");
        assertThat(dto1.getMarca().getNombre()).isEqualTo("Marca 1");
        assertThat(dto1.getCategoriaIva().getNombre()).isEqualTo("IVA 1");

        // una sola consulta por repositorio, con los ids de los dos productos
        verify(productoPrecioRepository, times(1)).findByIdIn(coleccionConIdsExactos("pre-1", "pre-2"));
        verify(productoInventarioRepository, times(1)).findByIdIn(coleccionConIdsExactos("inv-1", "inv-2"));
        verify(categoriaRepository, times(1)).findByIdIn(coleccionConIdsExactos("cat-1", "cat-2"));
        verify(subcategoriaRepository, times(1)).findByIdIn(coleccionConIdsExactos("sub-1", "sub-2"));
        verify(marcaRepository, times(1)).findByIdIn(coleccionConIdsExactos("m-1", "m-2"));
        verify(categoriaIVARepository, times(1)).findByIdIn(coleccionConIdsExactos("iva-1", "iva-2"));
        verify(productoImagenRepository, times(1)).findByProductoIdIn(
            coleccionConIdsExactos(new ObjectId(PRODUCTO_ID_1), new ObjectId(PRODUCTO_ID_2))
        );

        // nunca se resuelven relaciones de a una (eliminacion del N+1)
        verify(productoPrecioRepository, never()).findById(any());
        verify(productoInventarioRepository, never()).findById(any());
        verify(categoriaRepository, never()).findById(any());
        verify(subcategoriaRepository, never()).findById(any());
        verify(marcaRepository, never()).findById(any());
        verify(categoriaIVARepository, never()).findById(any());
    }

    @Test
    void findAllByIdsBuscaPorLoteYResuelveRelaciones() {
        Producto p1 = productoConRefs(PRODUCTO_ID_1, "pre-1", "inv-1", "cat-1", "sub-1", "m-1", "iva-1");
        when(productoRepository.findAllById(anyCollection())).thenReturn(List.of(p1));
        when(productoPrecioRepository.findByIdIn(anyCollection())).thenReturn(List.of(precioResuelto("pre-1", "9900.99")));
        when(productoInventarioRepository.findByIdIn(anyCollection())).thenReturn(List.of(inventarioResuelto("inv-1", 20)));
        when(categoriaRepository.findByIdIn(anyCollection())).thenReturn(List.of(categoriaResuelta("cat-1", "Categoria 1")));
        when(subcategoriaRepository.findByIdIn(anyCollection())).thenReturn(List.of(subcategoriaResuelta("sub-1", "Subcategoria 1")));
        when(marcaRepository.findByIdIn(anyCollection())).thenReturn(List.of(marcaResuelta("m-1", "Marca 1")));
        when(categoriaIVARepository.findByIdIn(anyCollection())).thenReturn(List.of(catIvaResuelta("iva-1", "IVA 1")));
        when(productoImagenRepository.findByProductoIdIn(anyCollection())).thenReturn(List.of());

        List<ProductoDTO> result = service.findAllByIds(List.of(PRODUCTO_ID_1));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(PRODUCTO_ID_1);
        assertThat(result.get(0).getPrecio().getPrecioVenta()).isEqualByComparingTo(new BigDecimal("9900.99"));
        verify(productoPrecioRepository, times(1)).findByIdIn(coleccionConIdsExactos("pre-1"));
        verify(productoRepository, times(1)).findAllById(coleccionConIdsExactos(PRODUCTO_ID_1));
    }

    @Test
    void searchActivePropagaElOrdenamientoPorPrecioVentaAlQuery() {
        Producto p1 = productoBasico(PRODUCTO_ID_1, new BigDecimal("10000.00"));
        Producto p2 = productoBasico(PRODUCTO_ID_2, new BigDecimal("5000.00"));
        when(mongoTemplate.find(any(Query.class), eq(Producto.class))).thenReturn(List.of(p1, p2));
        when(productoImagenRepository.findByProductoIdIn(anyCollection())).thenReturn(List.of());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "precioVenta"));
        Page<ProductoDTO> page = service.searchActive("", null, null, pageable);

        assertThat(page.getContent()).hasSize(2);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).find(queryCaptor.capture(), eq(Producto.class));
        Query query = queryCaptor.getValue();
        assertThat(query.getSortObject()).containsEntry("precioVenta", 1);
        verify(productoImagenRepository, times(1)).findByProductoIdIn(
            coleccionConIdsExactos(new ObjectId(PRODUCTO_ID_1), new ObjectId(PRODUCTO_ID_2))
        );
    }

    @Test
    void searchActivePropagaElOrdenamientoDescendente() {
        Producto p1 = productoBasico(PRODUCTO_ID_1, new BigDecimal("10000.00"));
        when(mongoTemplate.find(any(Query.class), eq(Producto.class))).thenReturn(List.of(p1));
        when(productoImagenRepository.findByProductoIdIn(anyCollection())).thenReturn(List.of());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "precioVenta"));
        service.searchActive("", null, null, pageable);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).find(queryCaptor.capture(), eq(Producto.class));
        assertThat(queryCaptor.getValue().getSortObject()).containsEntry("precioVenta", -1);
    }

    private Producto productoBasico(String id, BigDecimal precioVenta) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre("Producto " + id);
        producto.setSlug(id);
        producto.setSku(id);
        producto.setDestacado(false);
        producto.setActivo(true);
        producto.setPrecioVenta(precioVenta);
        return producto;
    }

    private Producto productoConRefs(
        String id,
        String precioId,
        String inventarioId,
        String categoriaId,
        String subcategoriaId,
        String marcaId,
        String ivaId
    ) {
        Producto producto = productoBasico(id, null);

        ProductoPrecio precio = new ProductoPrecio();
        precio.setId(precioId);
        producto.setPrecio(precio);

        ProductoInventario inventario = new ProductoInventario();
        inventario.setId(inventarioId);
        producto.setInventario(inventario);

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        producto.setCategoria(categoria);

        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setId(subcategoriaId);
        Categoria categoriaDeSubcategoria = new Categoria();
        categoriaDeSubcategoria.setId(categoriaId);
        subcategoria.setCategoria(categoriaDeSubcategoria);
        producto.setSubcategoria(subcategoria);

        Marca marca = new Marca();
        marca.setId(marcaId);
        producto.setMarca(marca);

        CategoriaIVA categoriaIva = new CategoriaIVA();
        categoriaIva.setId(ivaId);
        producto.setCategoriaIva(categoriaIva);

        return producto;
    }

    private ProductoPrecio precioResuelto(String id, String precioVenta) {
        ProductoPrecio precio = new ProductoPrecio();
        precio.setId(id);
        precio.setPrecioVenta(new BigDecimal(precioVenta));
        return precio;
    }

    private ProductoInventario inventarioResuelto(String id, Integer stock) {
        ProductoInventario inventario = new ProductoInventario();
        inventario.setId(id);
        inventario.setStock(stock);
        return inventario;
    }

    private Categoria categoriaResuelta(String id, String nombre) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        return categoria;
    }

    private Subcategoria subcategoriaResuelta(String id, String nombre) {
        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setId(id);
        subcategoria.setNombre(nombre);
        return subcategoria;
    }

    @Test
    void guardarProductoDenormalizaPrecioVentaDesdeElPrecioAsociado() {
        ProductoPrecio precio = new ProductoPrecio();
        precio.setId("pre-1");
        precio.setPrecioVenta(new BigDecimal("1500.5"));
        when(productoPrecioRepository.findById("pre-1")).thenReturn(java.util.Optional.of(precio));
        when(productoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        com.mycompany.knstore.service.dto.ProductoPrecioDTO precioDto = new com.mycompany.knstore.service.dto.ProductoPrecioDTO();
        precioDto.setId("pre-1");

        ProductoDTO dto = new ProductoDTO();
        dto.setId("p-1");
        dto.setNombre("Tenis de prueba");
        dto.setPrecio(precioDto);

        service.save(dto);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).updateFirst(queryCaptor.capture(), updateCaptor.capture(), eq(Producto.class));

        assertThat(queryCaptor.getValue().getQueryObject().get("_id")).isEqualTo("p-1");
        Object valorSet = ((java.util.Map<?, ?>) updateCaptor.getValue().getUpdateObject().get("$set")).get("precio_venta");
        assertThat(valorSet).isEqualTo(new BigDecimal("1500.50"));
    }

    private Marca marcaResuelta(String id, String nombre) {
        Marca marca = new Marca();
        marca.setId(id);
        marca.setNombre(nombre);
        return marca;
    }

    private CategoriaIVA catIvaResuelta(String id, String nombre) {
        CategoriaIVA categoriaIva = new CategoriaIVA();
        categoriaIva.setId(id);
        categoriaIva.setNombre(nombre);
        return categoriaIva;
    }

    private ProductoDTO buscarPorId(List<ProductoDTO> productos, String id) {
        return productos
            .stream()
            .filter(dto -> id.equals(dto.getId()))
            .findFirst()
            .orElseThrow();
    }

    @SafeVarargs
    private final <T> java.util.Collection<T> coleccionConIdsExactos(T... ids) {
        return org.mockito.ArgumentMatchers.argThat(
            coleccion -> coleccion != null && coleccion.size() == ids.length && Set.of(ids).containsAll(coleccion)
        );
    }
}
