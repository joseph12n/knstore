package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.ProductoImagen;
import com.mycompany.knstore.repository.ProductoImagenRepository;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.dto.ProductoImagenDTO;
import com.mycompany.knstore.service.mapper.ProductoImagenMapper;
import com.mycompany.knstore.service.mapper.ProductoImagenMapperImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoImagenServiceImplTest {

    private final ProductoImagenMapper productoImagenMapper = new ProductoImagenMapperImpl();

    @Mock
    private ProductoImagenRepository productoImagenRepository;

    private ProductoImagenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductoImagenServiceImpl(productoImagenRepository, productoImagenMapper);
    }

    private static ProductoDTO productoDto(String id) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        return dto;
    }

    @Test
    void savePersisteEntidadYRetornaDto() {
        ProductoImagenDTO dto = new ProductoImagenDTO();
        dto.setImagenContentType("image/jpeg");
        dto.setImagenAlt("Frente del zapato");
        dto.setEsPrincipal(true);
        dto.setImagenUrl("/img/producto-1/frente.jpg");
        dto.setProducto(productoDto("producto-1"));
        when(productoImagenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoImagenDTO result = service.save(dto);

        ArgumentCaptor<ProductoImagen> captor = ArgumentCaptor.forClass(ProductoImagen.class);
        verify(productoImagenRepository).save(captor.capture());
        assertThat(captor.getValue().getImagenContentType()).isEqualTo("image/jpeg");
        assertThat(captor.getValue().getImagenAlt()).isEqualTo("Frente del zapato");
        assertThat(captor.getValue().getEsPrincipal()).isTrue();
        assertThat(captor.getValue().getImagenUrl()).isEqualTo("/img/producto-1/frente.jpg");
        assertThat(captor.getValue().getProducto()).isNotNull();
        assertThat(captor.getValue().getProducto().getId()).isEqualTo("producto-1");
        assertThat(result.getImagenContentType()).isEqualTo("image/jpeg");
        assertThat(result.getEsPrincipal()).isTrue();
        assertThat(result.getImagenUrl()).isEqualTo("/img/producto-1/frente.jpg");
        assertThat(result.getProducto()).isNotNull();
        assertThat(result.getProducto().getId()).isEqualTo("producto-1");
    }

    @Test
    void updateGuardaEntidadConIdYRetornaDto() {
        ProductoImagenDTO dto = new ProductoImagenDTO();
        dto.setId("imagen-1");
        dto.setImagenContentType("image/webp");
        dto.setImagenAlt("Lateral");
        dto.setEsPrincipal(false);
        dto.setImagenUrl("/img/producto-1/lateral.webp");
        when(productoImagenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoImagenDTO result = service.update(dto);

        ArgumentCaptor<ProductoImagen> captor = ArgumentCaptor.forClass(ProductoImagen.class);
        verify(productoImagenRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("imagen-1");
        assertThat(captor.getValue().getImagenContentType()).isEqualTo("image/webp");
        assertThat(captor.getValue().getEsPrincipal()).isFalse();
        assertThat(result.getId()).isEqualTo("imagen-1");
        assertThat(result.getImagenContentType()).isEqualTo("image/webp");
        assertThat(result.getEsPrincipal()).isFalse();
    }

    @Test
    void partialUpdateAplicaSoloCamposNoNulos() {
        ProductoImagen existente = new ProductoImagen();
        existente.setId("imagen-1");
        existente.setImagenContentType("image/png");
        existente.setImagenAlt("Vieja descripcion");
        existente.setEsPrincipal(true);
        existente.setImagenUrl("/img/vieja.png");
        when(productoImagenRepository.findById("imagen-1")).thenReturn(Optional.of(existente));
        when(productoImagenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoImagenDTO dto = new ProductoImagenDTO();
        dto.setId("imagen-1");
        dto.setEsPrincipal(false);
        dto.setImagenUrl("/img/nueva.png");

        Optional<ProductoImagenDTO> result = service.partialUpdate(dto);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("imagen-1");
        ArgumentCaptor<ProductoImagen> captor = ArgumentCaptor.forClass(ProductoImagen.class);
        verify(productoImagenRepository).save(captor.capture());
        assertThat(captor.getValue().getEsPrincipal()).isFalse();
        assertThat(captor.getValue().getImagenUrl()).isEqualTo("/img/nueva.png");
        assertThat(captor.getValue().getImagenContentType()).isEqualTo("image/png");
        assertThat(captor.getValue().getImagenAlt()).isEqualTo("Vieja descripcion");
    }

    @Test
    void partialUpdateRetornaVacioCuandoNoExiste() {
        ProductoImagenDTO dto = new ProductoImagenDTO();
        dto.setId("no-existe");
        when(productoImagenRepository.findById("no-existe")).thenReturn(Optional.empty());

        Optional<ProductoImagenDTO> result = service.partialUpdate(dto);

        assertThat(result).isEmpty();
        verify(productoImagenRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void findOneRetornaDtoCuandoExiste() {
        ProductoImagen imagen = new ProductoImagen();
        imagen.setId("imagen-1");
        imagen.setImagenContentType("image/jpeg");
        imagen.setEsPrincipal(true);
        when(productoImagenRepository.findById("imagen-1")).thenReturn(Optional.of(imagen));

        Optional<ProductoImagenDTO> result = service.findOne("imagen-1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("imagen-1");
        assertThat(result.orElseThrow().getImagenContentType()).isEqualTo("image/jpeg");
        assertThat(result.orElseThrow().getEsPrincipal()).isTrue();
    }

    @Test
    void findOneRetornaVacioCuandoNoExiste() {
        when(productoImagenRepository.findById("no-existe")).thenReturn(Optional.empty());

        Optional<ProductoImagenDTO> result = service.findOne("no-existe");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaTodosLosDtos() {
        ProductoImagen principal = new ProductoImagen();
        principal.setId("imagen-1");
        principal.setEsPrincipal(true);
        ProductoImagen secundaria = new ProductoImagen();
        secundaria.setId("imagen-2");
        secundaria.setEsPrincipal(false);
        when(productoImagenRepository.findAll()).thenReturn(List.of(principal, secundaria));

        List<ProductoImagenDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductoImagenDTO::getId).containsExactly("imagen-1", "imagen-2");
        assertThat(result).extracting(ProductoImagenDTO::getEsPrincipal).containsExactly(true, false);
    }

    @Test
    void deleteEliminaPorId() {
        service.delete("imagen-1");

        verify(productoImagenRepository).deleteById("imagen-1");
    }
}
