package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.ItemPedidoAsserts.*;
import static com.mycompany.knstore.domain.ItemPedidoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemPedidoMapperTest {

    private ItemPedidoMapper itemPedidoMapper;

    @BeforeEach
    void setUp() {
        itemPedidoMapper = new ItemPedidoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getItemPedidoSample1();
        var actual = itemPedidoMapper.toEntity(itemPedidoMapper.toDto(expected));
        assertItemPedidoAllPropertiesEquals(expected, actual);
    }
}
