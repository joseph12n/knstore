package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CuentaTestSamples.*;
import static com.mycompany.knstore.domain.DireccionTestSamples.*;
import static com.mycompany.knstore.domain.PedidoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DireccionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Direccion.class);
        Direccion direccion1 = getDireccionSample1();
        Direccion direccion2 = new Direccion();
        assertThat(direccion1).isNotEqualTo(direccion2);

        direccion2.setId(direccion1.getId());
        assertThat(direccion1).isEqualTo(direccion2);

        direccion2 = getDireccionSample2();
        assertThat(direccion1).isNotEqualTo(direccion2);
    }

    @Test
    void cuentaTest() {
        Direccion direccion = getDireccionRandomSampleGenerator();
        Cuenta cuentaBack = getCuentaRandomSampleGenerator();

        direccion.setCuenta(cuentaBack);
        assertThat(direccion.getCuenta()).isEqualTo(cuentaBack);

        direccion.cuenta(null);
        assertThat(direccion.getCuenta()).isNull();
    }

    @Test
    void pedidoTest() {
        Direccion direccion = getDireccionRandomSampleGenerator();
        Pedido pedidoBack = getPedidoRandomSampleGenerator();

        direccion.setPedido(pedidoBack);
        assertThat(direccion.getPedido()).isEqualTo(pedidoBack);
        assertThat(pedidoBack.getDireccion()).isEqualTo(direccion);

        direccion.pedido(null);
        assertThat(direccion.getPedido()).isNull();
        assertThat(pedidoBack.getDireccion()).isNull();
    }
}
