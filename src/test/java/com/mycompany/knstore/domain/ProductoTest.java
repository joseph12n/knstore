package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static com.mycompany.knstore.domain.SubcategoriaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
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
    void subcategoriaTest() {
        Producto producto = getProductoRandomSampleGenerator();
        Subcategoria subcategoriaBack = getSubcategoriaRandomSampleGenerator();

        producto.setSubcategoria(subcategoriaBack);
        assertThat(producto.getSubcategoria()).isEqualTo(subcategoriaBack);

        producto.subcategoria(null);
        assertThat(producto.getSubcategoria()).isNull();
    }
}
