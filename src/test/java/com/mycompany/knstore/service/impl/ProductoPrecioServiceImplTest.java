package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import com.mycompany.knstore.service.mapper.ProductoPrecioMapper;
import com.mycompany.knstore.service.mapper.ProductoPrecioMapperImpl;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoPrecioServiceImplTest {

    private final ProductoPrecioMapper productoPrecioMapper = new ProductoPrecioMapperImpl();

    @Mock
    private ProductoPrecioRepository productoPrecioRepository;

    private ProductoPrecioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductoPrecioServiceImpl(productoPrecioRepository, productoPrecioMapper);
    }

    @Test
    void guardarCalculaGananciaYNormalizaValores() {
        when(productoPrecioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoPrecioDTO dto = new ProductoPrecioDTO();
        dto.setPrecioCompra(new BigDecimal("150000.005"));
        dto.setPrecioVenta(new BigDecimal("280000"));
        dto.setPrecioAdicional(BigDecimal.ZERO);

        ProductoPrecioDTO result = service.save(dto);

        assertThat(result.getPrecioCompra()).isEqualByComparingTo(new BigDecimal("150000.01"));
        assertThat(result.getGanancia()).isEqualByComparingTo(new BigDecimal("129999.99"));
        assertThat(result.getGanancia().scale()).isEqualTo(2);
    }
}
