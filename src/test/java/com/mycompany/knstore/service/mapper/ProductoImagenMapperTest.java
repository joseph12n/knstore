package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.ProductoImagenAsserts.*;
import static com.mycompany.knstore.domain.ProductoImagenTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductoImagenMapperTest {

    private ProductoImagenMapper productoImagenMapper;

    @BeforeEach
    void setUp() {
        productoImagenMapper = new ProductoImagenMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductoImagenSample1();
        var actual = productoImagenMapper.toEntity(productoImagenMapper.toDto(expected));
        assertProductoImagenAllPropertiesEquals(expected, actual);
    }
}
