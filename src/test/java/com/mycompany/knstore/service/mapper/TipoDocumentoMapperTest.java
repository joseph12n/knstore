package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.TipoDocumentoAsserts.*;
import static com.mycompany.knstore.domain.TipoDocumentoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TipoDocumentoMapperTest {

    private TipoDocumentoMapper tipoDocumentoMapper;

    @BeforeEach
    void setUp() {
        tipoDocumentoMapper = new TipoDocumentoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTipoDocumentoSample1();
        var actual = tipoDocumentoMapper.toEntity(tipoDocumentoMapper.toDto(expected));
        assertTipoDocumentoAllPropertiesEquals(expected, actual);
    }
}
