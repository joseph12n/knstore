package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CarritoTestSamples.*;
import static com.mycompany.knstore.domain.CuentaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarritoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Carrito.class);
        Carrito carrito1 = getCarritoSample1();
        Carrito carrito2 = new Carrito();
        assertThat(carrito1).isNotEqualTo(carrito2);

        carrito2.setId(carrito1.getId());
        assertThat(carrito1).isEqualTo(carrito2);

        carrito2 = getCarritoSample2();
        assertThat(carrito1).isNotEqualTo(carrito2);
    }

    @Test
    void cuentaTest() {
        Carrito carrito = getCarritoRandomSampleGenerator();
        Cuenta cuentaBack = getCuentaRandomSampleGenerator();

        carrito.setCuenta(cuentaBack);
        assertThat(carrito.getCuenta()).isEqualTo(cuentaBack);

        carrito.cuenta(null);
        assertThat(carrito.getCuenta()).isNull();
    }
}
