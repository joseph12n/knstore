package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static com.mycompany.knstore.domain.VarianteProductoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VarianteProductoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VarianteProducto.class);
        VarianteProducto varianteProducto1 = getVarianteProductoSample1();
        VarianteProducto varianteProducto2 = new VarianteProducto();
        assertThat(varianteProducto1).isNotEqualTo(varianteProducto2);

        varianteProducto2.setId(varianteProducto1.getId());
        assertThat(varianteProducto1).isEqualTo(varianteProducto2);

        varianteProducto2 = getVarianteProductoSample2();
        assertThat(varianteProducto1).isNotEqualTo(varianteProducto2);
    }

    @Test
    void productoTest() {
        VarianteProducto varianteProducto = getVarianteProductoRandomSampleGenerator();
        Producto productoBack = getProductoRandomSampleGenerator();

        varianteProducto.setProducto(productoBack);
        assertThat(varianteProducto.getProducto()).isEqualTo(productoBack);

        varianteProducto.producto(null);
        assertThat(varianteProducto.getProducto()).isNull();
    }
}
