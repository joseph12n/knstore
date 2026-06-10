package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CuentaTestSamples.*;
import static com.mycompany.knstore.domain.TipoDocumentoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CuentaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cuenta.class);
        Cuenta cuenta1 = getCuentaSample1();
        Cuenta cuenta2 = new Cuenta();
        assertThat(cuenta1).isNotEqualTo(cuenta2);

        cuenta2.setId(cuenta1.getId());
        assertThat(cuenta1).isEqualTo(cuenta2);

        cuenta2 = getCuentaSample2();
        assertThat(cuenta1).isNotEqualTo(cuenta2);
    }

    @Test
    void tipoDocumentoTest() {
        Cuenta cuenta = getCuentaRandomSampleGenerator();
        TipoDocumento tipoDocumentoBack = getTipoDocumentoRandomSampleGenerator();

        cuenta.setTipoDocumento(tipoDocumentoBack);
        assertThat(cuenta.getTipoDocumento()).isEqualTo(tipoDocumentoBack);

        cuenta.tipoDocumento(null);
        assertThat(cuenta.getTipoDocumento()).isNull();
    }
}
