package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.EnvioTestSamples.*;
import static com.mycompany.knstore.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnvioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Envio.class);
        Envio envio1 = getEnvioSample1();
        Envio envio2 = new Envio();
        assertThat(envio1).isNotEqualTo(envio2);

        envio2.setId(envio1.getId());
        assertThat(envio1).isEqualTo(envio2);

        envio2 = getEnvioSample2();
        assertThat(envio1).isNotEqualTo(envio2);
    }

    @Test
    void pedidoTest() {
        Envio envio = getEnvioRandomSampleGenerator();
        Pedido pedidoBack = getPedidoRandomSampleGenerator();

        envio.setPedido(pedidoBack);
        assertThat(envio.getPedido()).isEqualTo(pedidoBack);

        envio.pedido(null);
        assertThat(envio.getPedido()).isNull();
    }
}
