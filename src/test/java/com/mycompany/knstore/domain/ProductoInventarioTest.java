package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.ProductoInventarioTestSamples.*;
import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductoInventarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductoInventario.class);
        ProductoInventario productoInventario1 = getProductoInventarioSample1();
        ProductoInventario productoInventario2 = new ProductoInventario();
        assertThat(productoInventario1).isNotEqualTo(productoInventario2);

        productoInventario2.setId(productoInventario1.getId());
        assertThat(productoInventario1).isEqualTo(productoInventario2);

        productoInventario2 = getProductoInventarioSample2();
        assertThat(productoInventario1).isNotEqualTo(productoInventario2);
    }

    @Test
    void productoTest() {
        ProductoInventario productoInventario = getProductoInventarioRandomSampleGenerator();
        Producto productoBack = getProductoRandomSampleGenerator();

        productoInventario.setProducto(productoBack);
        assertThat(productoInventario.getProducto()).isEqualTo(productoBack);
        assertThat(productoBack.getInventario()).isEqualTo(productoInventario);

        productoInventario.producto(null);
        assertThat(productoInventario.getProducto()).isNull();
        assertThat(productoBack.getInventario()).isNull();
    }
}
