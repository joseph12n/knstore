package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DireccionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DireccionDTO.class);
        DireccionDTO direccionDTO1 = new DireccionDTO();
        direccionDTO1.setId("id1");
        DireccionDTO direccionDTO2 = new DireccionDTO();
        assertThat(direccionDTO1).isNotEqualTo(direccionDTO2);
        direccionDTO2.setId(direccionDTO1.getId());
        assertThat(direccionDTO1).isEqualTo(direccionDTO2);
        direccionDTO2.setId("id2");
        assertThat(direccionDTO1).isNotEqualTo(direccionDTO2);
        direccionDTO1.setId(null);
        assertThat(direccionDTO1).isNotEqualTo(direccionDTO2);
    }
}
