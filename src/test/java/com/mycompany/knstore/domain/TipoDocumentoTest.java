package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.TipoDocumentoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TipoDocumentoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoDocumento.class);
        TipoDocumento tipoDocumento1 = getTipoDocumentoSample1();
        TipoDocumento tipoDocumento2 = new TipoDocumento();
        assertThat(tipoDocumento1).isNotEqualTo(tipoDocumento2);

        tipoDocumento2.setId(tipoDocumento1.getId());
        assertThat(tipoDocumento1).isEqualTo(tipoDocumento2);

        tipoDocumento2 = getTipoDocumentoSample2();
        assertThat(tipoDocumento1).isNotEqualTo(tipoDocumento2);
    }
}
