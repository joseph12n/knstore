package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.PedidoAsserts.*;
import static com.mycompany.knstore.domain.PedidoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PedidoMapperTest {

    private PedidoMapper pedidoMapper;

    @BeforeEach
    void setUp() {
        pedidoMapper = new PedidoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPedidoSample1();
        var actual = pedidoMapper.toEntity(pedidoMapper.toDto(expected));
        assertPedidoAllPropertiesEquals(expected, actual);
    }
}
