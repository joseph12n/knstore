package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EtiquetaProductoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtiquetaProductoDTO.class);
        EtiquetaProductoDTO etiquetaProductoDTO1 = new EtiquetaProductoDTO();
        etiquetaProductoDTO1.setId("id1");
        EtiquetaProductoDTO etiquetaProductoDTO2 = new EtiquetaProductoDTO();
        assertThat(etiquetaProductoDTO1).isNotEqualTo(etiquetaProductoDTO2);
        etiquetaProductoDTO2.setId(etiquetaProductoDTO1.getId());
        assertThat(etiquetaProductoDTO1).isEqualTo(etiquetaProductoDTO2);
        etiquetaProductoDTO2.setId("id2");
        assertThat(etiquetaProductoDTO1).isNotEqualTo(etiquetaProductoDTO2);
        etiquetaProductoDTO1.setId(null);
        assertThat(etiquetaProductoDTO1).isNotEqualTo(etiquetaProductoDTO2);
    }
}
