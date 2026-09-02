package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import com.mycompany.knstore.service.mapper.ProductoPrecioMapper;
import com.mycompany.knstore.service.mapper.ProductoPrecioMapperImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class ProductoPrecioServiceImplTest {

    private final ProductoPrecioMapper productoPrecioMapper = new ProductoPrecioMapperImpl();

    @Mock
    private ProductoPrecioRepository productoPrecioRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private ProductoPrecioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductoPrecioServiceImpl(productoPrecioRepository, productoPrecioMapper, mongoTemplate);
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

    @Test
    void guardarSinProductoAsociadoNoSincronizaPrecioVenta() {
        when(productoPrecioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoPrecioDTO dto = new ProductoPrecioDTO();
        dto.setPrecioCompra(new BigDecimal("1000"));
        dto.setPrecioVenta(new BigDecimal("1500"));
        dto.setPrecioAdicional(BigDecimal.ZERO);

        service.save(dto);

        verify(mongoTemplate, never()).updateFirst(any(Query.class), any(Update.class), any(Class.class));
    }

    @Test
    void guardarSincronizaPrecioVentaEnProducto() {
        Producto producto = new Producto();
        producto.setId("p-1");

        ProductoPrecio existente = new ProductoPrecio();
        existente.setId("pre-1");
        existente.setProducto(producto);

        when(productoPrecioRepository.findById("pre-1")).thenReturn(Optional.of(existente));
        when(productoPrecioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoPrecioDTO dto = new ProductoPrecioDTO();
        dto.setId("pre-1");
        dto.setPrecioCompra(new BigDecimal("1000"));
        dto.setPrecioVenta(new BigDecimal("1500.5"));
        dto.setPrecioAdicional(BigDecimal.ZERO);

        ProductoPrecioDTO result = service.partialUpdate(dto).orElseThrow();

        assertThat(result.getPrecioVenta()).isEqualByComparingTo(new BigDecimal("1500.50"));

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).updateFirst(queryCaptor.capture(), updateCaptor.capture(), eq(Producto.class));

        assertThat(queryCaptor.getValue().getQueryObject().get("_id")).isEqualTo("p-1");
        Object valorSet = ((java.util.Map<?, ?>) updateCaptor.getValue().getUpdateObject().get("$set")).get("precio_venta");
        assertThat(valorSet).isEqualTo(new BigDecimal("1500.50"));
    }
}
