package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.CategoriaIVATestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoriaIVATest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoriaIVA.class);
        CategoriaIVA categoriaIVA1 = getCategoriaIVASample1();
        CategoriaIVA categoriaIVA2 = new CategoriaIVA();
        assertThat(categoriaIVA1).isNotEqualTo(categoriaIVA2);

        categoriaIVA2.setId(categoriaIVA1.getId());
        assertThat(categoriaIVA1).isEqualTo(categoriaIVA2);

        categoriaIVA2 = getCategoriaIVASample2();
        assertThat(categoriaIVA1).isNotEqualTo(categoriaIVA2);
    }
}
