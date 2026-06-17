package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CuentaTestSamples.*;
import static com.mycompany.knstore.domain.DireccionTestSamples.*;
import static com.mycompany.knstore.domain.EnvioTestSamples.*;
import static com.mycompany.knstore.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PedidoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pedido.class);
        Pedido pedido1 = getPedidoSample1();
        Pedido pedido2 = new Pedido();
        assertThat(pedido1).isNotEqualTo(pedido2);

        pedido2.setId(pedido1.getId());
        assertThat(pedido1).isEqualTo(pedido2);

        pedido2 = getPedidoSample2();
        assertThat(pedido1).isNotEqualTo(pedido2);
    }

    @Test
    void direccionTest() {
        Pedido pedido = getPedidoRandomSampleGenerator();
        Direccion direccionBack = getDireccionRandomSampleGenerator();

        pedido.setDireccion(direccionBack);
        assertThat(pedido.getDireccion()).isEqualTo(direccionBack);

        pedido.direccion(null);
        assertThat(pedido.getDireccion()).isNull();
    }

    @Test
    void cuentaTest() {
        Pedido pedido = getPedidoRandomSampleGenerator();
        Cuenta cuentaBack = getCuentaRandomSampleGenerator();

        pedido.setCuenta(cuentaBack);
        assertThat(pedido.getCuenta()).isEqualTo(cuentaBack);

        pedido.cuenta(null);
        assertThat(pedido.getCuenta()).isNull();
    }

    @Test
    void envioTest() {
        Pedido pedido = getPedidoRandomSampleGenerator();
        Envio envioBack = getEnvioRandomSampleGenerator();

        pedido.setEnvio(envioBack);
        assertThat(pedido.getEnvio()).isEqualTo(envioBack);
        assertThat(envioBack.getPedido()).isEqualTo(pedido);

        pedido.envio(null);
        assertThat(pedido.getEnvio()).isNull();
        assertThat(envioBack.getPedido()).isNull();
    }
}
