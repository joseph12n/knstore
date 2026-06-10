package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.PagoAsserts.*;
import static com.mycompany.knstore.domain.PagoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PagoMapperTest {

    private PagoMapper pagoMapper;

    @BeforeEach
    void setUp() {
        pagoMapper = new PagoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPagoSample1();
        var actual = pagoMapper.toEntity(pagoMapper.toDto(expected));
        assertPagoAllPropertiesEquals(expected, actual);
    }
}
