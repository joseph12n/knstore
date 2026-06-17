package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MarcaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MarcaDTO.class);
        MarcaDTO marcaDTO1 = new MarcaDTO();
        marcaDTO1.setId("id1");
        MarcaDTO marcaDTO2 = new MarcaDTO();
        assertThat(marcaDTO1).isNotEqualTo(marcaDTO2);
        marcaDTO2.setId(marcaDTO1.getId());
        assertThat(marcaDTO1).isEqualTo(marcaDTO2);
        marcaDTO2.setId("id2");
        assertThat(marcaDTO1).isNotEqualTo(marcaDTO2);
        marcaDTO1.setId(null);
        assertThat(marcaDTO1).isNotEqualTo(marcaDTO2);
    }
}
