package com.mycompany.knstore.service.mapper;

import static com.mycompany.knstore.domain.ItemCarritoAsserts.*;
import static com.mycompany.knstore.domain.ItemCarritoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemCarritoMapperTest {

    private ItemCarritoMapper itemCarritoMapper;

    @BeforeEach
    void setUp() {
        itemCarritoMapper = new ItemCarritoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getItemCarritoSample1();
        var actual = itemCarritoMapper.toEntity(itemCarritoMapper.toDto(expected));
        assertItemCarritoAllPropertiesEquals(expected, actual);
    }
}
