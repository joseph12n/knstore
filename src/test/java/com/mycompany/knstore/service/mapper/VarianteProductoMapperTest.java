package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.VarianteProductoAsserts.*;
import static com.mycompany.knstore.domain.VarianteProductoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VarianteProductoMapperTest {

    private VarianteProductoMapper varianteProductoMapper;

    @BeforeEach
    void setUp() {
        varianteProductoMapper = new VarianteProductoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVarianteProductoSample1();
        var actual = varianteProductoMapper.toEntity(varianteProductoMapper.toDto(expected));
        assertVarianteProductoAllPropertiesEquals(expected, actual);
    }
}
