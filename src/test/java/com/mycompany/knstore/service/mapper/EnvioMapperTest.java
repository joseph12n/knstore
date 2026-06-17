package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.EnvioAsserts.*;
import static com.mycompany.knstore.domain.EnvioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnvioMapperTest {

    private EnvioMapper envioMapper;

    @BeforeEach
    void setUp() {
        envioMapper = new EnvioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEnvioSample1();
        var actual = envioMapper.toEntity(envioMapper.toDto(expected));
        assertEnvioAllPropertiesEquals(expected, actual);
    }
}
