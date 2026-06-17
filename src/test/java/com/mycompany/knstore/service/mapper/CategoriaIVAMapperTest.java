package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.CategoriaIVAAsserts.*;
import static com.mycompany.knstore.domain.CategoriaIVATestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoriaIVAMapperTest {

    private CategoriaIVAMapper categoriaIVAMapper;

    @BeforeEach
    void setUp() {
        categoriaIVAMapper = new CategoriaIVAMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCategoriaIVASample1();
        var actual = categoriaIVAMapper.toEntity(categoriaIVAMapper.toDto(expected));
        assertCategoriaIVAAllPropertiesEquals(expected, actual);
    }
}
