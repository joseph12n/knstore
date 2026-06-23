package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductoImagenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductoImagenDTO.class);
        ProductoImagenDTO productoImagenDTO1 = new ProductoImagenDTO();
        productoImagenDTO1.setId("id1");
        ProductoImagenDTO productoImagenDTO2 = new ProductoImagenDTO();
        assertThat(productoImagenDTO1).isNotEqualTo(productoImagenDTO2);
        productoImagenDTO2.setId(productoImagenDTO1.getId());
        assertThat(productoImagenDTO1).isEqualTo(productoImagenDTO2);
        productoImagenDTO2.setId("id2");
        assertThat(productoImagenDTO1).isNotEqualTo(productoImagenDTO2);
        productoImagenDTO1.setId(null);
        assertThat(productoImagenDTO1).isNotEqualTo(productoImagenDTO2);
    }
}
