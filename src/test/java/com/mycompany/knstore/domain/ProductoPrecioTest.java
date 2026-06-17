package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.ProductoPrecioTestSamples.*;
import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductoPrecioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductoPrecio.class);
        ProductoPrecio productoPrecio1 = getProductoPrecioSample1();
        ProductoPrecio productoPrecio2 = new ProductoPrecio();
        assertThat(productoPrecio1).isNotEqualTo(productoPrecio2);

        productoPrecio2.setId(productoPrecio1.getId());
        assertThat(productoPrecio1).isEqualTo(productoPrecio2);

        productoPrecio2 = getProductoPrecioSample2();
        assertThat(productoPrecio1).isNotEqualTo(productoPrecio2);
    }

    @Test
    void productoTest() {
        ProductoPrecio productoPrecio = getProductoPrecioRandomSampleGenerator();
        Producto productoBack = getProductoRandomSampleGenerator();

        productoPrecio.setProducto(productoBack);
        assertThat(productoPrecio.getProducto()).isEqualTo(productoBack);
        assertThat(productoBack.getPrecio()).isEqualTo(productoPrecio);

        productoPrecio.producto(null);
        assertThat(productoPrecio.getProducto()).isNull();
        assertThat(productoBack.getPrecio()).isNull();
    }
}
