package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CuentaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CuentaDTO.class);
        CuentaDTO cuentaDTO1 = new CuentaDTO();
        cuentaDTO1.setId("id1");
        CuentaDTO cuentaDTO2 = new CuentaDTO();
        assertThat(cuentaDTO1).isNotEqualTo(cuentaDTO2);
        cuentaDTO2.setId(cuentaDTO1.getId());
        assertThat(cuentaDTO1).isEqualTo(cuentaDTO2);
        cuentaDTO2.setId("id2");
        assertThat(cuentaDTO1).isNotEqualTo(cuentaDTO2);
        cuentaDTO1.setId(null);
        assertThat(cuentaDTO1).isNotEqualTo(cuentaDTO2);
    }
}
