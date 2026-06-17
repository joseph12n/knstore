package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemPedidoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemPedidoDTO.class);
        ItemPedidoDTO itemPedidoDTO1 = new ItemPedidoDTO();
        itemPedidoDTO1.setId("id1");
        ItemPedidoDTO itemPedidoDTO2 = new ItemPedidoDTO();
        assertThat(itemPedidoDTO1).isNotEqualTo(itemPedidoDTO2);
        itemPedidoDTO2.setId(itemPedidoDTO1.getId());
        assertThat(itemPedidoDTO1).isEqualTo(itemPedidoDTO2);
        itemPedidoDTO2.setId("id2");
        assertThat(itemPedidoDTO1).isNotEqualTo(itemPedidoDTO2);
        itemPedidoDTO1.setId(null);
        assertThat(itemPedidoDTO1).isNotEqualTo(itemPedidoDTO2);
    }
}
