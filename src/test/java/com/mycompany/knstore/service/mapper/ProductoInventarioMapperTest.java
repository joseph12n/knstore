package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.ProductoInventarioAsserts.*;
import static com.mycompany.knstore.domain.ProductoInventarioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductoInventarioMapperTest {

    private ProductoInventarioMapper productoInventarioMapper;

    @BeforeEach
    void setUp() {
        productoInventarioMapper = new ProductoInventarioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductoInventarioSample1();
        var actual = productoInventarioMapper.toEntity(productoInventarioMapper.toDto(expected));
        assertProductoInventarioAllPropertiesEquals(expected, actual);
    }
}
