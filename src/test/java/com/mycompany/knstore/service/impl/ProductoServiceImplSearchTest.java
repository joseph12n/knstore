package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.domain.Producto;
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
import com.mycompany.knstore.service.mapper.ProductoMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplSearchTest {

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
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void buscarPublicoFiltraPorTextoYPrecioConPaginacion() {
        Categoria categoria = new Categoria();
        categoria.setId("cat-1");

        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setId("sub-1");

        Marca nike = new Marca();
        nike.setId("marca-1");
        nike.setNombre("Nike");

        Marca puma = new Marca();
        puma.setId("marca-2");
        puma.setNombre("Puma");

        Producto p1 = producto(
            "p1",
            "Tenis Running",
            "tenis-running",
            "SKU-1",
            "Deporte",
            true,
            categoria,
            subcategoria,
            nike,
            new BigDecimal("120000")
        );
        Producto p2 = producto(
            "p2",
            "Camiseta Algodon",
            "camiseta",
            "SKU-2",
            "Casual",
            true,
            categoria,
            subcategoria,
            puma,
            new BigDecimal("55000")
        );
        Producto p3 = producto(
            "p3",
            "Tenis Urbano",
            "tenis-urbano",
            "SKU-3",
            "Lifestyle",
            false,
            categoria,
            subcategoria,
            nike,
            new BigDecimal("95000")
        );

        when(productoRepository.findAllWithEagerRelationships()).thenReturn(List.of(p1, p2, p3));
        when(productoMapper.toDto(p1)).thenReturn(dto("p1", "Tenis Running"));

        Page<ProductoDTO> result = productoService.buscarPublico(
            "tenis",
            "cat-1",
            null,
            "marca-1",
            new BigDecimal("90000"),
            new BigDecimal("130000"),
            null,
            true,
            PageRequest.of(0, 10, Sort.by("nombre").ascending())
        );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("p1");
    }

    private Producto producto(
        String id,
        String nombre,
        String slug,
        String sku,
        String descripcion,
        boolean activo,
        Categoria categoria,
        Subcategoria subcategoria,
        Marca marca,
        BigDecimal precioVenta
    ) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setSlug(slug);
        producto.setSku(sku);
        producto.setDescripcion(descripcion);
        producto.setActivo(activo);
        producto.setDestacado(false);
        producto.setCategoria(categoria);
        producto.setSubcategoria(subcategoria);
        producto.setMarca(marca);

        ProductoPrecio precio = new ProductoPrecio();
        precio.setPrecioVenta(precioVenta);
        producto.setPrecio(precio);
        return producto;
    }

    private ProductoDTO dto(String id, String nombre) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        return dto;
    }
}
