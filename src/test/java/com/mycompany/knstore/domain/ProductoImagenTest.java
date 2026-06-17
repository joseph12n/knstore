package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.ProductoImagenTestSamples.*;
import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductoImagenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductoImagen.class);
        ProductoImagen productoImagen1 = getProductoImagenSample1();
        ProductoImagen productoImagen2 = new ProductoImagen();
        assertThat(productoImagen1).isNotEqualTo(productoImagen2);

        productoImagen2.setId(productoImagen1.getId());
        assertThat(productoImagen1).isEqualTo(productoImagen2);

        productoImagen2 = getProductoImagenSample2();
        assertThat(productoImagen1).isNotEqualTo(productoImagen2);
    }

    @Test
    void productoTest() {
        ProductoImagen productoImagen = getProductoImagenRandomSampleGenerator();
        Producto productoBack = getProductoRandomSampleGenerator();

        productoImagen.setProducto(productoBack);
        assertThat(productoImagen.getProducto()).isEqualTo(productoBack);

        productoImagen.producto(null);
        assertThat(productoImagen.getProducto()).isNull();
    }
}
