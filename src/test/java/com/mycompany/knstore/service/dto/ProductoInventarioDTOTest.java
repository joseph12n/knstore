package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductoInventarioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductoInventarioDTO.class);
        ProductoInventarioDTO productoInventarioDTO1 = new ProductoInventarioDTO();
        productoInventarioDTO1.setId("id1");
        ProductoInventarioDTO productoInventarioDTO2 = new ProductoInventarioDTO();
        assertThat(productoInventarioDTO1).isNotEqualTo(productoInventarioDTO2);
        productoInventarioDTO2.setId(productoInventarioDTO1.getId());
        assertThat(productoInventarioDTO1).isEqualTo(productoInventarioDTO2);
        productoInventarioDTO2.setId("id2");
        assertThat(productoInventarioDTO1).isNotEqualTo(productoInventarioDTO2);
        productoInventarioDTO1.setId(null);
        assertThat(productoInventarioDTO1).isNotEqualTo(productoInventarioDTO2);
    }
}
