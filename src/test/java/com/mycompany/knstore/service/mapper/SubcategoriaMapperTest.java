package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.SubcategoriaAsserts.*;
import static com.mycompany.knstore.domain.SubcategoriaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubcategoriaMapperTest {

    private SubcategoriaMapper subcategoriaMapper;

    @BeforeEach
    void setUp() {
        subcategoriaMapper = new SubcategoriaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSubcategoriaSample1();
        var actual = subcategoriaMapper.toEntity(subcategoriaMapper.toDto(expected));
        assertSubcategoriaAllPropertiesEquals(expected, actual);
    }
}
