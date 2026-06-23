package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.EtiquetaProductoAsserts.*;
import static com.mycompany.knstore.domain.EtiquetaProductoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EtiquetaProductoMapperTest {

    private EtiquetaProductoMapper etiquetaProductoMapper;

    @BeforeEach
    void setUp() {
        etiquetaProductoMapper = new EtiquetaProductoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEtiquetaProductoSample1();
        var actual = etiquetaProductoMapper.toEntity(etiquetaProductoMapper.toDto(expected));
        assertEtiquetaProductoAllPropertiesEquals(expected, actual);
    }
}
