package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarritoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarritoDTO.class);
        CarritoDTO carritoDTO1 = new CarritoDTO();
        carritoDTO1.setId("id1");
        CarritoDTO carritoDTO2 = new CarritoDTO();
        assertThat(carritoDTO1).isNotEqualTo(carritoDTO2);
        carritoDTO2.setId(carritoDTO1.getId());
        assertThat(carritoDTO1).isEqualTo(carritoDTO2);
        carritoDTO2.setId("id2");
        assertThat(carritoDTO1).isNotEqualTo(carritoDTO2);
        carritoDTO1.setId(null);
        assertThat(carritoDTO1).isNotEqualTo(carritoDTO2);
    }
}
