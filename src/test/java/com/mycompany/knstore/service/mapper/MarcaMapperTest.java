package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.MarcaAsserts.*;
import static com.mycompany.knstore.domain.MarcaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarcaMapperTest {

    private MarcaMapper marcaMapper;

    @BeforeEach
    void setUp() {
        marcaMapper = new MarcaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMarcaSample1();
        var actual = marcaMapper.toEntity(marcaMapper.toDto(expected));
        assertMarcaAllPropertiesEquals(expected, actual);
    }
}
