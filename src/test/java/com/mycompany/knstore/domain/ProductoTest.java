package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CategoriaIVATestSamples.*;
import static com.mycompany.knstore.domain.CategoriaTestSamples.*;
import static com.mycompany.knstore.domain.MarcaTestSamples.*;
import static com.mycompany.knstore.domain.ProductoImagenTestSamples.*;
import static com.mycompany.knstore.domain.ProductoInventarioTestSamples.*;
import static com.mycompany.knstore.domain.ProductoPrecioTestSamples.*;
import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static com.mycompany.knstore.domain.SubcategoriaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Producto.class);
        Producto producto1 = getProductoSample1();
        Producto producto2 = new Producto();
        assertThat(producto1).isNotEqualTo(producto2);

        producto2.setId(producto1.getId());
        assertThat(producto1).isEqualTo(producto2);

        producto2 = getProductoSample2();
        assertThat(producto1).isNotEqualTo(producto2);
    }

    @Test
    void precioTest() {
        Producto producto = getProductoRandomSampleGenerator();
        ProductoPrecio productoPrecioBack = getProductoPrecioRandomSampleGenerator();

        producto.setPrecio(productoPrecioBack);
        assertThat(producto.getPrecio()).isEqualTo(productoPrecioBack);

        producto.precio(null);
        assertThat(producto.getPrecio()).isNull();
    }

    @Test
    void inventarioTest() {
        Producto producto = getProductoRandomSampleGenerator();
        ProductoInventario productoInventarioBack = getProductoInventarioRandomSampleGenerator();

        producto.setInventario(productoInventarioBack);
        assertThat(producto.getInventario()).isEqualTo(productoInventarioBack);

        producto.inventario(null);
        assertThat(producto.getInventario()).isNull();
    }

    @Test
    void imagenesTest() {
        Producto producto = getProductoRandomSampleGenerator();
        ProductoImagen productoImagenBack = getProductoImagenRandomSampleGenerator();

        producto.addImagenes(productoImagenBack);
        assertThat(producto.getImageneses()).containsOnly(productoImagenBack);
        assertThat(productoImagenBack.getProducto()).isEqualTo(producto);

        producto.removeImagenes(productoImagenBack);
        assertThat(producto.getImageneses()).doesNotContain(productoImagenBack);
        assertThat(productoImagenBack.getProducto()).isNull();

        producto.imageneses(new HashSet<>(Set.of(productoImagenBack)));
        assertThat(producto.getImageneses()).containsOnly(productoImagenBack);
        assertThat(productoImagenBack.getProducto()).isEqualTo(producto);

        producto.setImageneses(new HashSet<>());
        assertThat(producto.getImageneses()).doesNotContain(productoImagenBack);
        assertThat(productoImagenBack.getProducto()).isNull();
    }

    @Test
    void categoriaTest() {
        Producto producto = getProductoRandomSampleGenerator();
        Categoria categoriaBack = getCategoriaRandomSampleGenerator();

        producto.setCategoria(categoriaBack);
        assertThat(producto.getCategoria()).isEqualTo(categoriaBack);

        producto.categoria(null);
        assertThat(producto.getCategoria()).isNull();
    }

    @Test
    void subcategoriaTest() {
        Producto producto = getProductoRandomSampleGenerator();
        Subcategoria subcategoriaBack = getSubcategoriaRandomSampleGenerator();

        producto.setSubcategoria(subcategoriaBack);
        assertThat(producto.getSubcategoria()).isEqualTo(subcategoriaBack);

        producto.subcategoria(null);
        assertThat(producto.getSubcategoria()).isNull();
    }

    @Test
    void marcaTest() {
        Producto producto = getProductoRandomSampleGenerator();
        Marca marcaBack = getMarcaRandomSampleGenerator();

        producto.setMarca(marcaBack);
        assertThat(producto.getMarca()).isEqualTo(marcaBack);

        producto.marca(null);
        assertThat(producto.getMarca()).isNull();
    }

    @Test
    void categoriaIvaTest() {
        Producto producto = getProductoRandomSampleGenerator();
        CategoriaIVA categoriaIVABack = getCategoriaIVARandomSampleGenerator();

        producto.setCategoriaIva(categoriaIVABack);
        assertThat(producto.getCategoriaIva()).isEqualTo(categoriaIVABack);

        producto.categoriaIva(null);
        assertThat(producto.getCategoriaIva()).isNull();
    }
}
