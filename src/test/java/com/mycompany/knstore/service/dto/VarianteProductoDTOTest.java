package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VarianteProductoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VarianteProductoDTO.class);
        VarianteProductoDTO varianteProductoDTO1 = new VarianteProductoDTO();
        varianteProductoDTO1.setId("id1");
        VarianteProductoDTO varianteProductoDTO2 = new VarianteProductoDTO();
        assertThat(varianteProductoDTO1).isNotEqualTo(varianteProductoDTO2);
        varianteProductoDTO2.setId(varianteProductoDTO1.getId());
        assertThat(varianteProductoDTO1).isEqualTo(varianteProductoDTO2);
        varianteProductoDTO2.setId("id2");
        assertThat(varianteProductoDTO1).isNotEqualTo(varianteProductoDTO2);
        varianteProductoDTO1.setId(null);
        assertThat(varianteProductoDTO1).isNotEqualTo(varianteProductoDTO2);
    }
}
