package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductoPrecioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductoPrecioDTO.class);
        ProductoPrecioDTO productoPrecioDTO1 = new ProductoPrecioDTO();
        productoPrecioDTO1.setId("id1");
        ProductoPrecioDTO productoPrecioDTO2 = new ProductoPrecioDTO();
        assertThat(productoPrecioDTO1).isNotEqualTo(productoPrecioDTO2);
        productoPrecioDTO2.setId(productoPrecioDTO1.getId());
        assertThat(productoPrecioDTO1).isEqualTo(productoPrecioDTO2);
        productoPrecioDTO2.setId("id2");
        assertThat(productoPrecioDTO1).isNotEqualTo(productoPrecioDTO2);
        productoPrecioDTO1.setId(null);
        assertThat(productoPrecioDTO1).isNotEqualTo(productoPrecioDTO2);
    }
}
