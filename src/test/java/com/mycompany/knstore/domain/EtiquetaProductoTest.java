package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.EtiquetaProductoTestSamples.*;
import static com.mycompany.knstore.domain.ProductoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EtiquetaProductoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtiquetaProducto.class);
        EtiquetaProducto etiquetaProducto1 = getEtiquetaProductoSample1();
        EtiquetaProducto etiquetaProducto2 = new EtiquetaProducto();
        assertThat(etiquetaProducto1).isNotEqualTo(etiquetaProducto2);

        etiquetaProducto2.setId(etiquetaProducto1.getId());
        assertThat(etiquetaProducto1).isEqualTo(etiquetaProducto2);

        etiquetaProducto2 = getEtiquetaProductoSample2();
        assertThat(etiquetaProducto1).isNotEqualTo(etiquetaProducto2);
    }

    @Test
    void productoTest() {
        EtiquetaProducto etiquetaProducto = getEtiquetaProductoRandomSampleGenerator();
        Producto productoBack = getProductoRandomSampleGenerator();

        etiquetaProducto.setProducto(productoBack);
        assertThat(etiquetaProducto.getProducto()).isEqualTo(productoBack);

        etiquetaProducto.producto(null);
        assertThat(etiquetaProducto.getProducto()).isNull();
    }
}
