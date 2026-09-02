package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.EtiquetaProducto;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.EtiquetaProductoRepository;
import com.mycompany.knstore.service.dto.EtiquetaProductoDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.EtiquetaProductoMapper;
import com.mycompany.knstore.service.mapper.EtiquetaProductoMapperImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class EtiquetaProductoServiceImplTest {

    private final EtiquetaProductoMapper etiquetaProductoMapper = new EtiquetaProductoMapperImpl();

    @Mock
    private EtiquetaProductoRepository etiquetaProductoRepository;

    private EtiquetaProductoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EtiquetaProductoServiceImpl(etiquetaProductoRepository, etiquetaProductoMapper);
    }

    @Test
    void saveMapeaYGuardaLaEtiquetaConSuProducto() {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId("prod-1");
        productoDTO.setNombre("Tenis Air Max");
        EtiquetaProductoDTO dto = new EtiquetaProductoDTO();
        dto.setEtiqueta("oferta");
        dto.setProducto(productoDTO);
        when(etiquetaProductoRepository.save(any())).thenAnswer(invocation -> {
            EtiquetaProducto guardada = invocation.getArgument(0);
            guardada.setId("etq-1");
            return guardada;
        });

        EtiquetaProductoDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo("etq-1");
        assertThat(result.getEtiqueta()).isEqualTo("oferta");
        assertThat(result.getProducto().getId()).isEqualTo("prod-1");
        assertThat(result.getProducto().getNombre()).isEqualTo("Tenis Air Max");
        verify(etiquetaProductoRepository).save(any());
    }

    @Test
    void updateMapeaYGuardaLaEtiquetaConSusDatosActualizados() {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId("prod-2");
        productoDTO.setNombre("Zapato Cuero");
        EtiquetaProductoDTO dto = new EtiquetaProductoDTO();
        dto.setId("etq-1");
        dto.setEtiqueta("nuevo");
        dto.setProducto(productoDTO);
        when(etiquetaProductoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EtiquetaProductoDTO result = service.update(dto);

        assertThat(result.getId()).isEqualTo("etq-1");
        assertThat(result.getEtiqueta()).isEqualTo("nuevo");
        assertThat(result.getProducto().getId()).isEqualTo("prod-2");
        assertThat(result.getProducto().getNombre()).isEqualTo("Zapato Cuero");
    }

    @Test
    void partialUpdateActualizaSoloCamposNoNulos() {
        Producto producto = new Producto();
        producto.setId("prod-1");
        producto.setNombre("Tenis Air Max");
        EtiquetaProducto existente = new EtiquetaProducto();
        existente.setId("etq-1");
        existente.setEtiqueta("oferta");
        existente.setProducto(producto);
        when(etiquetaProductoRepository.findById("etq-1")).thenReturn(Optional.of(existente));
        when(etiquetaProductoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EtiquetaProductoDTO cambios = new EtiquetaProductoDTO();
        cambios.setId("etq-1");
        cambios.setEtiqueta("liquidacion");

        Optional<EtiquetaProductoDTO> result = service.partialUpdate(cambios);

        assertThat(result).isPresent();
        assertThat(result.get().getEtiqueta()).isEqualTo("liquidacion");
        assertThat(result.get().getProducto().getId()).isEqualTo("prod-1");
        assertThat(result.get().getProducto().getNombre()).isEqualTo("Tenis Air Max");
    }

    @Test
    void partialUpdateCuandoNoExisteLaEtiquetaRetornaVacioYNoGuarda() {
        when(etiquetaProductoRepository.findById("etq-404")).thenReturn(Optional.empty());

        EtiquetaProductoDTO cambios = new EtiquetaProductoDTO();
        cambios.setId("etq-404");
        cambios.setEtiqueta("Inexistente");

        Optional<EtiquetaProductoDTO> result = service.partialUpdate(cambios);

        assertThat(result).isEmpty();
        verify(etiquetaProductoRepository, never()).save(any());
    }

    @Test
    void findOneUsaLaConsultaConRelacionesYRetornaElDTOMapeado() {
        Producto producto = new Producto();
        producto.setId("prod-1");
        producto.setNombre("Tenis Air Max");
        EtiquetaProducto etiqueta = new EtiquetaProducto();
        etiqueta.setId("etq-1");
        etiqueta.setEtiqueta("oferta");
        etiqueta.setProducto(producto);
        when(etiquetaProductoRepository.findOneWithEagerRelationships("etq-1")).thenReturn(Optional.of(etiqueta));

        Optional<EtiquetaProductoDTO> result = service.findOne("etq-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("etq-1");
        assertThat(result.get().getEtiqueta()).isEqualTo("oferta");
        assertThat(result.get().getProducto().getId()).isEqualTo("prod-1");
        assertThat(result.get().getProducto().getNombre()).isEqualTo("Tenis Air Max");
        verify(etiquetaProductoRepository).findOneWithEagerRelationships("etq-1");
    }

    @Test
    void findOneCuandoNoExisteRetornaVacio() {
        when(etiquetaProductoRepository.findOneWithEagerRelationships("etq-404")).thenReturn(Optional.empty());

        Optional<EtiquetaProductoDTO> result = service.findOne("etq-404");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaLaPaginaDeEtiquetasMapeadasADTO() {
        EtiquetaProducto oferta = new EtiquetaProducto();
        oferta.setId("etq-1");
        oferta.setEtiqueta("oferta");
        EtiquetaProducto nuevo = new EtiquetaProducto();
        nuevo.setId("etq-2");
        nuevo.setEtiqueta("nuevo");
        when(etiquetaProductoRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(oferta, nuevo)));

        Page<EtiquetaProductoDTO> result = service.findAll(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(EtiquetaProductoDTO::getEtiqueta).containsExactly("oferta", "nuevo");
    }

    @Test
    void findAllWithEagerRelationshipsRetornaLaPaginaMapeadaConSusProductos() {
        Producto producto = new Producto();
        producto.setId("prod-1");
        producto.setNombre("Tenis Air Max");
        EtiquetaProducto etiqueta = new EtiquetaProducto();
        etiqueta.setId("etq-1");
        etiqueta.setEtiqueta("oferta");
        etiqueta.setProducto(producto);
        when(etiquetaProductoRepository.findAllWithEagerRelationships(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(etiqueta)));

        Page<EtiquetaProductoDTO> result = service.findAllWithEagerRelationships(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEtiqueta()).isEqualTo("oferta");
        assertThat(result.getContent().get(0).getProducto().getId()).isEqualTo("prod-1");
    }

    @Test
    void deleteEliminaLaEtiquetaPorId() {
        service.delete("etq-1");

        verify(etiquetaProductoRepository).deleteById("etq-1");
    }
}
