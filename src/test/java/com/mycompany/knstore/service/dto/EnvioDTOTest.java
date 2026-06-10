package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnvioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvioDTO.class);
        EnvioDTO envioDTO1 = new EnvioDTO();
        envioDTO1.setId("id1");
        EnvioDTO envioDTO2 = new EnvioDTO();
        assertThat(envioDTO1).isNotEqualTo(envioDTO2);
        envioDTO2.setId(envioDTO1.getId());
        assertThat(envioDTO1).isEqualTo(envioDTO2);
        envioDTO2.setId("id2");
        assertThat(envioDTO1).isNotEqualTo(envioDTO2);
        envioDTO1.setId(null);
        assertThat(envioDTO1).isNotEqualTo(envioDTO2);
    }
}
