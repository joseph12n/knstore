package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.PagoTestSamples.*;
import static com.mycompany.knstore.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PagoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pago.class);
        Pago pago1 = getPagoSample1();
        Pago pago2 = new Pago();
        assertThat(pago1).isNotEqualTo(pago2);

        pago2.setId(pago1.getId());
        assertThat(pago1).isEqualTo(pago2);

        pago2 = getPagoSample2();
        assertThat(pago1).isNotEqualTo(pago2);
    }

    @Test
    void pedidoTest() {
        Pago pago = getPagoRandomSampleGenerator();
        Pedido pedidoBack = getPedidoRandomSampleGenerator();

        pago.setPedido(pedidoBack);
        assertThat(pago.getPedido()).isEqualTo(pedidoBack);

        pago.pedido(null);
        assertThat(pago.getPedido()).isNull();
    }
}
