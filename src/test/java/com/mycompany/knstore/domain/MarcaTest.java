package com.mycompany.knstore.domain;

import static com.mycompany.knstore.domain.MarcaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MarcaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Marca.class);
        Marca marca1 = getMarcaSample1();
        Marca marca2 = new Marca();
        assertThat(marca1).isNotEqualTo(marca2);

        marca2.setId(marca1.getId());
        assertThat(marca1).isEqualTo(marca2);

        marca2 = getMarcaSample2();
        assertThat(marca1).isNotEqualTo(marca2);
    }
}
