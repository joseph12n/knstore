package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.ProductoPrecioAsserts.*;
import static com.mycompany.knstore.domain.ProductoPrecioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductoPrecioMapperTest {

    private ProductoPrecioMapper productoPrecioMapper;

    @BeforeEach
    void setUp() {
        productoPrecioMapper = new ProductoPrecioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductoPrecioSample1();
        var actual = productoPrecioMapper.toEntity(productoPrecioMapper.toDto(expected));
        assertProductoPrecioAllPropertiesEquals(expected, actual);
    }
}
