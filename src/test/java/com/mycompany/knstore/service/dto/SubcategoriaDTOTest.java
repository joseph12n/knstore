package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubcategoriaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubcategoriaDTO.class);
        SubcategoriaDTO subcategoriaDTO1 = new SubcategoriaDTO();
        subcategoriaDTO1.setId("id1");
        SubcategoriaDTO subcategoriaDTO2 = new SubcategoriaDTO();
        assertThat(subcategoriaDTO1).isNotEqualTo(subcategoriaDTO2);
        subcategoriaDTO2.setId(subcategoriaDTO1.getId());
        assertThat(subcategoriaDTO1).isEqualTo(subcategoriaDTO2);
        subcategoriaDTO2.setId("id2");
        assertThat(subcategoriaDTO1).isNotEqualTo(subcategoriaDTO2);
        subcategoriaDTO1.setId(null);
        assertThat(subcategoriaDTO1).isNotEqualTo(subcategoriaDTO2);
    }
}
