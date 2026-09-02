package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.enumeration.EstadoIVA;
import com.mycompany.knstore.repository.CategoriaIVARepository;
import com.mycompany.knstore.service.dto.CategoriaIVADTO;
import com.mycompany.knstore.service.mapper.CategoriaIVAMapper;
import com.mycompany.knstore.service.mapper.CategoriaIVAMapperImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoriaIVAServiceImplTest {

    private final CategoriaIVAMapper categoriaIVAMapper = new CategoriaIVAMapperImpl();

    @Mock
    private CategoriaIVARepository categoriaIVARepository;

    private CategoriaIVAServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CategoriaIVAServiceImpl(categoriaIVARepository, categoriaIVAMapper);
    }

    @Test
    void saveMapeaYGuardaLaCategoriaIVARetornandoElDTOPersistido() {
        CategoriaIVADTO dto = new CategoriaIVADTO();
        dto.setNombre("IVA 19");
        dto.setPorcentaje(new BigDecimal("19.00"));
        dto.setEstado(EstadoIVA.ACTIVO);
        when(categoriaIVARepository.save(any())).thenAnswer(invocation -> {
            CategoriaIVA guardada = invocation.getArgument(0);
            guardada.setId("iva-1");
            return guardada;
        });

        CategoriaIVADTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo("iva-1");
        assertThat(result.getNombre()).isEqualTo("IVA 19");
        assertThat(result.getPorcentaje()).isEqualByComparingTo("19.00");
        assertThat(result.getEstado()).isEqualTo(EstadoIVA.ACTIVO);
        verify(categoriaIVARepository).save(any());
    }

    @Test
    void updateMapeaYGuardaLaCategoriaIVAConSusDatosActualizados() {
        CategoriaIVADTO dto = new CategoriaIVADTO();
        dto.setId("iva-1");
        dto.setNombre("IVA 5");
        dto.setPorcentaje(new BigDecimal("5.00"));
        dto.setEstado(EstadoIVA.INACTIVO);
        when(categoriaIVARepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaIVADTO result = service.update(dto);

        assertThat(result.getId()).isEqualTo("iva-1");
        assertThat(result.getNombre()).isEqualTo("IVA 5");
        assertThat(result.getPorcentaje()).isEqualByComparingTo("5.00");
        assertThat(result.getEstado()).isEqualTo(EstadoIVA.INACTIVO);
    }

    @Test
    void partialUpdateActualizaSoloCamposNoNulos() {
        CategoriaIVA existente = new CategoriaIVA();
        existente.setId("iva-1");
        existente.setNombre("IVA 19");
        existente.setPorcentaje(new BigDecimal("19.00"));
        existente.setEstado(EstadoIVA.ACTIVO);
        when(categoriaIVARepository.findById("iva-1")).thenReturn(Optional.of(existente));
        when(categoriaIVARepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaIVADTO cambios = new CategoriaIVADTO();
        cambios.setId("iva-1");
        cambios.setPorcentaje(new BigDecimal("5.00"));

        Optional<CategoriaIVADTO> result = service.partialUpdate(cambios);

        assertThat(result).isPresent();
        assertThat(result.get().getPorcentaje()).isEqualByComparingTo("5.00");
        assertThat(result.get().getNombre()).isEqualTo("IVA 19");
        assertThat(result.get().getEstado()).isEqualTo(EstadoIVA.ACTIVO);
    }

    @Test
    void partialUpdateCuandoNoExisteLaCategoriaIVARetornaVacioYNoGuarda() {
        when(categoriaIVARepository.findById("iva-404")).thenReturn(Optional.empty());

        CategoriaIVADTO cambios = new CategoriaIVADTO();
        cambios.setId("iva-404");
        cambios.setNombre("Inexistente");

        Optional<CategoriaIVADTO> result = service.partialUpdate(cambios);

        assertThat(result).isEmpty();
        verify(categoriaIVARepository, never()).save(any());
    }

    @Test
    void findOneCuandoExisteRetornaElDTOMapeado() {
        CategoriaIVA categoriaIVA = new CategoriaIVA();
        categoriaIVA.setId("iva-1");
        categoriaIVA.setNombre("IVA 19");
        categoriaIVA.setPorcentaje(new BigDecimal("19.00"));
        categoriaIVA.setEstado(EstadoIVA.ACTIVO);
        when(categoriaIVARepository.findById("iva-1")).thenReturn(Optional.of(categoriaIVA));

        Optional<CategoriaIVADTO> result = service.findOne("iva-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("iva-1");
        assertThat(result.get().getNombre()).isEqualTo("IVA 19");
        assertThat(result.get().getPorcentaje()).isEqualByComparingTo("19.00");
        assertThat(result.get().getEstado()).isEqualTo(EstadoIVA.ACTIVO);
    }

    @Test
    void findOneCuandoNoExisteRetornaVacio() {
        when(categoriaIVARepository.findById("iva-404")).thenReturn(Optional.empty());

        Optional<CategoriaIVADTO> result = service.findOne("iva-404");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaLaListaDeCategoriasIVAMapeadasADTO() {
        CategoriaIVA exento = new CategoriaIVA();
        exento.setId("iva-1");
        exento.setNombre("Exento");
        exento.setPorcentaje(BigDecimal.ZERO);
        exento.setEstado(EstadoIVA.ACTIVO);
        CategoriaIVA general = new CategoriaIVA();
        general.setId("iva-2");
        general.setNombre("IVA 19");
        general.setPorcentaje(new BigDecimal("19.00"));
        general.setEstado(EstadoIVA.ACTIVO);
        when(categoriaIVARepository.findAll()).thenReturn(List.of(exento, general));

        List<CategoriaIVADTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CategoriaIVADTO::getNombre).containsExactly("Exento", "IVA 19");
        assertThat(result.get(0).getPorcentaje()).isEqualByComparingTo("0");
    }

    @Test
    void deleteEliminaLaCategoriaIVAPorId() {
        service.delete("iva-1");

        verify(categoriaIVARepository).deleteById("iva-1");
    }
}
