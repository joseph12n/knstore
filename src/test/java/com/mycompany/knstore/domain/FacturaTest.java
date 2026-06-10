package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.FacturaTestSamples.*;
import static com.mycompany.knstore.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FacturaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Factura.class);
        Factura factura1 = getFacturaSample1();
        Factura factura2 = new Factura();
        assertThat(factura1).isNotEqualTo(factura2);

        factura2.setId(factura1.getId());
        assertThat(factura1).isEqualTo(factura2);

        factura2 = getFacturaSample2();
        assertThat(factura1).isNotEqualTo(factura2);
    }

    @Test
    void pedidoTest() {
        Factura factura = getFacturaRandomSampleGenerator();
        Pedido pedidoBack = getPedidoRandomSampleGenerator();

        factura.setPedido(pedidoBack);
        assertThat(factura.getPedido()).isEqualTo(pedidoBack);

        factura.pedido(null);
        assertThat(factura.getPedido()).isNull();
    }
}
