package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoriaIVADTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoriaIVADTO.class);
        CategoriaIVADTO categoriaIVADTO1 = new CategoriaIVADTO();
        categoriaIVADTO1.setId("id1");
        CategoriaIVADTO categoriaIVADTO2 = new CategoriaIVADTO();
        assertThat(categoriaIVADTO1).isNotEqualTo(categoriaIVADTO2);
        categoriaIVADTO2.setId(categoriaIVADTO1.getId());
        assertThat(categoriaIVADTO1).isEqualTo(categoriaIVADTO2);
        categoriaIVADTO2.setId("id2");
        assertThat(categoriaIVADTO1).isNotEqualTo(categoriaIVADTO2);
        categoriaIVADTO1.setId(null);
        assertThat(categoriaIVADTO1).isNotEqualTo(categoriaIVADTO2);
    }
}
