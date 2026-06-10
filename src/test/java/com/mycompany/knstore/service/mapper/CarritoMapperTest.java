package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.CarritoAsserts.*;
import static com.mycompany.knstore.domain.CarritoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarritoMapperTest {

    private CarritoMapper carritoMapper;

    @BeforeEach
    void setUp() {
        carritoMapper = new CarritoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarritoSample1();
        var actual = carritoMapper.toEntity(carritoMapper.toDto(expected));
        assertCarritoAllPropertiesEquals(expected, actual);
    }
}
