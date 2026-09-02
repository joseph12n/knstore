package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.enumeration.UbicacionBodega;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.service.dto.ProductoInventarioDTO;
import com.mycompany.knstore.service.mapper.ProductoInventarioMapper;
import com.mycompany.knstore.service.mapper.ProductoInventarioMapperImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoInventarioServiceImplTest {

    private final ProductoInventarioMapper productoInventarioMapper = new ProductoInventarioMapperImpl();

    @Mock
    private ProductoInventarioRepository productoInventarioRepository;

    private ProductoInventarioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductoInventarioServiceImpl(productoInventarioRepository, productoInventarioMapper);
    }

    @Test
    void savePersisteEntidadYRetornaDto() {
        ProductoInventarioDTO dto = new ProductoInventarioDTO();
        dto.setStock(15);
        dto.setStockMinimo(3);
        dto.setUbicacionBodega(UbicacionBodega.BODEGA_PRINCIPAL);
        dto.setGarantiaMeses(12);
        when(productoInventarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoInventarioDTO result = service.save(dto);

        ArgumentCaptor<ProductoInventario> captor = ArgumentCaptor.forClass(ProductoInventario.class);
        verify(productoInventarioRepository).save(captor.capture());
        assertThat(captor.getValue().getStock()).isEqualTo(15);
        assertThat(captor.getValue().getStockMinimo()).isEqualTo(3);
        assertThat(captor.getValue().getUbicacionBodega()).isEqualTo(UbicacionBodega.BODEGA_PRINCIPAL);
        assertThat(captor.getValue().getGarantiaMeses()).isEqualTo(12);
        assertThat(result.getStock()).isEqualTo(15);
        assertThat(result.getStockMinimo()).isEqualTo(3);
        assertThat(result.getUbicacionBodega()).isEqualTo(UbicacionBodega.BODEGA_PRINCIPAL);
        assertThat(result.getGarantiaMeses()).isEqualTo(12);
    }

    @Test
    void updateGuardaEntidadConIdYRetornaDto() {
        ProductoInventarioDTO dto = new ProductoInventarioDTO();
        dto.setId("inventario-1");
        dto.setStock(7);
        dto.setStockMinimo(1);
        dto.setUbicacionBodega(UbicacionBodega.EXHIBICION);
        when(productoInventarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoInventarioDTO result = service.update(dto);

        ArgumentCaptor<ProductoInventario> captor = ArgumentCaptor.forClass(ProductoInventario.class);
        verify(productoInventarioRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("inventario-1");
        assertThat(captor.getValue().getStock()).isEqualTo(7);
        assertThat(captor.getValue().getUbicacionBodega()).isEqualTo(UbicacionBodega.EXHIBICION);
        assertThat(result.getId()).isEqualTo("inventario-1");
        assertThat(result.getStock()).isEqualTo(7);
    }

    @Test
    void partialUpdateAplicaSoloCamposNoNulos() {
        ProductoInventario existente = new ProductoInventario();
        existente.setId("inventario-1");
        existente.setStock(15);
        existente.setStockMinimo(3);
        existente.setUbicacionBodega(UbicacionBodega.BODEGA_PRINCIPAL);
        existente.setGarantiaMeses(12);
        when(productoInventarioRepository.findById("inventario-1")).thenReturn(Optional.of(existente));
        when(productoInventarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoInventarioDTO dto = new ProductoInventarioDTO();
        dto.setId("inventario-1");
        dto.setStock(4);

        Optional<ProductoInventarioDTO> result = service.partialUpdate(dto);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("inventario-1");
        ArgumentCaptor<ProductoInventario> captor = ArgumentCaptor.forClass(ProductoInventario.class);
        verify(productoInventarioRepository).save(captor.capture());
        assertThat(captor.getValue().getStock()).isEqualTo(4);
        assertThat(captor.getValue().getStockMinimo()).isEqualTo(3);
        assertThat(captor.getValue().getUbicacionBodega()).isEqualTo(UbicacionBodega.BODEGA_PRINCIPAL);
        assertThat(captor.getValue().getGarantiaMeses()).isEqualTo(12);
    }

    @Test
    void partialUpdateRetornaVacioCuandoNoExiste() {
        ProductoInventarioDTO dto = new ProductoInventarioDTO();
        dto.setId("no-existe");
        when(productoInventarioRepository.findById("no-existe")).thenReturn(Optional.empty());

        Optional<ProductoInventarioDTO> result = service.partialUpdate(dto);

        assertThat(result).isEmpty();
        verify(productoInventarioRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void findOneRetornaDtoCuandoExiste() {
        ProductoInventario inventario = new ProductoInventario();
        inventario.setId("inventario-1");
        inventario.setStock(15);
        inventario.setUbicacionBodega(UbicacionBodega.BODEGA_SUR);
        when(productoInventarioRepository.findById("inventario-1")).thenReturn(Optional.of(inventario));

        Optional<ProductoInventarioDTO> result = service.findOne("inventario-1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("inventario-1");
        assertThat(result.orElseThrow().getStock()).isEqualTo(15);
        assertThat(result.orElseThrow().getUbicacionBodega()).isEqualTo(UbicacionBodega.BODEGA_SUR);
    }

    @Test
    void findOneRetornaVacioCuandoNoExiste() {
        when(productoInventarioRepository.findById("no-existe")).thenReturn(Optional.empty());

        Optional<ProductoInventarioDTO> result = service.findOne("no-existe");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaTodosLosDtos() {
        ProductoInventario primero = new ProductoInventario();
        primero.setId("inventario-1");
        primero.setStock(15);
        ProductoInventario segundo = new ProductoInventario();
        segundo.setId("inventario-2");
        segundo.setStock(0);
        when(productoInventarioRepository.findAll()).thenReturn(List.of(primero, segundo));

        List<ProductoInventarioDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductoInventarioDTO::getId).containsExactly("inventario-1", "inventario-2");
        assertThat(result).extracting(ProductoInventarioDTO::getStock).containsExactly(15, 0);
    }

    @Test
    void findAllWhereProductoIsNullFiltraInventariosSinProducto() {
        Producto producto = new Producto();
        producto.setId("producto-1");

        ProductoInventario conProducto = new ProductoInventario();
        conProducto.setId("inventario-1");
        conProducto.setStock(15);
        conProducto.setProducto(producto);

        ProductoInventario sinProducto = new ProductoInventario();
        sinProducto.setId("inventario-2");
        sinProducto.setStock(5);

        when(productoInventarioRepository.findAll()).thenReturn(List.of(conProducto, sinProducto));

        List<ProductoInventarioDTO> result = service.findAllWhereProductoIsNull();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("inventario-2");
    }

    @Test
    void deleteEliminaPorId() {
        service.delete("inventario-1");

        verify(productoInventarioRepository).deleteById("inventario-1");
    }
}
