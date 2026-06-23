package com.mycompany.knstore.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ItemCarritoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ItemCarritoDTO.class);
        ItemCarritoDTO itemCarritoDTO1 = new ItemCarritoDTO();
        itemCarritoDTO1.setId("id1");
        ItemCarritoDTO itemCarritoDTO2 = new ItemCarritoDTO();
        assertThat(itemCarritoDTO1).isNotEqualTo(itemCarritoDTO2);
        itemCarritoDTO2.setId(itemCarritoDTO1.getId());
        assertThat(itemCarritoDTO1).isEqualTo(itemCarritoDTO2);
        itemCarritoDTO2.setId("id2");
        assertThat(itemCarritoDTO1).isNotEqualTo(itemCarritoDTO2);
        itemCarritoDTO1.setId(null);
        assertThat(itemCarritoDTO1).isNotEqualTo(itemCarritoDTO2);
    }
}
