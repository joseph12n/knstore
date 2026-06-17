package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.CategoriaAsserts.*;
import static com.mycompany.knstore.domain.CategoriaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoriaMapperTest {

    private CategoriaMapper categoriaMapper;

    @BeforeEach
    void setUp() {
        categoriaMapper = new CategoriaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCategoriaSample1();
        var actual = categoriaMapper.toEntity(categoriaMapper.toDto(expected));
        assertCategoriaAllPropertiesEquals(expected, actual);
    }
}
