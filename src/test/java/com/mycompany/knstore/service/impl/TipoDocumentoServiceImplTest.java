package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.TipoDocumento;
import com.mycompany.knstore.domain.enumeration.EstadoTipoDocumento;
import com.mycompany.knstore.repository.TipoDocumentoRepository;
import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import com.mycompany.knstore.service.mapper.TipoDocumentoMapper;
import com.mycompany.knstore.service.mapper.TipoDocumentoMapperImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoServiceImplTest {

    private final TipoDocumentoMapper tipoDocumentoMapper = new TipoDocumentoMapperImpl();

    @Mock
    private TipoDocumentoRepository tipoDocumentoRepository;

    private TipoDocumentoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TipoDocumentoServiceImpl(tipoDocumentoRepository, tipoDocumentoMapper);
    }

    @Test
    void saveMapeaYGuardaElTipoDocumentoRetornandoElDTOPersistido() {
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.setSigla("CC");
        dto.setNombreTipo("Cedula");
        dto.setEstado(EstadoTipoDocumento.ACTIVO);
        when(tipoDocumentoRepository.save(any())).thenAnswer(invocation -> {
            TipoDocumento guardado = invocation.getArgument(0);
            guardado.setId("tdoc-1");
            return guardado;
        });

        TipoDocumentoDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo("tdoc-1");
        assertThat(result.getSigla()).isEqualTo("CC");
        assertThat(result.getNombreTipo()).isEqualTo("Cedula");
        assertThat(result.getEstado()).isEqualTo(EstadoTipoDocumento.ACTIVO);
        verify(tipoDocumentoRepository).save(any());
    }

    @Test
    void updateMapeaYGuardaElTipoDocumentoConSusDatosActualizados() {
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.setId("tdoc-1");
        dto.setSigla("NIT");
        dto.setNombreTipo("Nit");
        dto.setEstado(EstadoTipoDocumento.INACTIVO);
        when(tipoDocumentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TipoDocumentoDTO result = service.update(dto);

        assertThat(result.getId()).isEqualTo("tdoc-1");
        assertThat(result.getSigla()).isEqualTo("NIT");
        assertThat(result.getNombreTipo()).isEqualTo("Nit");
        assertThat(result.getEstado()).isEqualTo(EstadoTipoDocumento.INACTIVO);
    }

    @Test
    void partialUpdateActualizaSoloCamposNoNulos() {
        TipoDocumento existente = new TipoDocumento();
        existente.setId("tdoc-1");
        existente.setSigla("CC");
        existente.setNombreTipo("Cedula");
        existente.setEstado(EstadoTipoDocumento.ACTIVO);
        when(tipoDocumentoRepository.findById("tdoc-1")).thenReturn(Optional.of(existente));
        when(tipoDocumentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TipoDocumentoDTO cambios = new TipoDocumentoDTO();
        cambios.setId("tdoc-1");
        cambios.setNombreTipo("Cedula ciudadania");

        Optional<TipoDocumentoDTO> result = service.partialUpdate(cambios);

        assertThat(result).isPresent();
        assertThat(result.get().getNombreTipo()).isEqualTo("Cedula ciudadania");
        assertThat(result.get().getSigla()).isEqualTo("CC");
        assertThat(result.get().getEstado()).isEqualTo(EstadoTipoDocumento.ACTIVO);
    }

    @Test
    void partialUpdateCuandoNoExisteElTipoDocumentoRetornaVacioYNoGuarda() {
        when(tipoDocumentoRepository.findById("tdoc-404")).thenReturn(Optional.empty());

        TipoDocumentoDTO cambios = new TipoDocumentoDTO();
        cambios.setId("tdoc-404");
        cambios.setSigla("XX");

        Optional<TipoDocumentoDTO> result = service.partialUpdate(cambios);

        assertThat(result).isEmpty();
        verify(tipoDocumentoRepository, never()).save(any());
    }

    @Test
    void findOneCuandoExisteRetornaElDTOMapeado() {
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId("tdoc-1");
        tipoDocumento.setSigla("CC");
        tipoDocumento.setNombreTipo("Cedula");
        tipoDocumento.setEstado(EstadoTipoDocumento.ACTIVO);
        when(tipoDocumentoRepository.findById("tdoc-1")).thenReturn(Optional.of(tipoDocumento));

        Optional<TipoDocumentoDTO> result = service.findOne("tdoc-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("tdoc-1");
        assertThat(result.get().getSigla()).isEqualTo("CC");
        assertThat(result.get().getNombreTipo()).isEqualTo("Cedula");
        assertThat(result.get().getEstado()).isEqualTo(EstadoTipoDocumento.ACTIVO);
    }

    @Test
    void findOneCuandoNoExisteRetornaVacio() {
        when(tipoDocumentoRepository.findById("tdoc-404")).thenReturn(Optional.empty());

        Optional<TipoDocumentoDTO> result = service.findOne("tdoc-404");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaLaListaDeTiposDocumentoMapeadasADTO() {
        TipoDocumento cedula = new TipoDocumento();
        cedula.setId("tdoc-1");
        cedula.setSigla("CC");
        cedula.setNombreTipo("Cedula");
        cedula.setEstado(EstadoTipoDocumento.ACTIVO);
        TipoDocumento nit = new TipoDocumento();
        nit.setId("tdoc-2");
        nit.setSigla("NIT");
        nit.setNombreTipo("Nit");
        nit.setEstado(EstadoTipoDocumento.INACTIVO);
        when(tipoDocumentoRepository.findAll()).thenReturn(List.of(cedula, nit));

        List<TipoDocumentoDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TipoDocumentoDTO::getSigla).containsExactly("CC", "NIT");
        assertThat(result.get(1).getEstado()).isEqualTo(EstadoTipoDocumento.INACTIVO);
    }

    @Test
    void deleteEliminaElTipoDocumentoPorId() {
        service.delete("tdoc-1");

        verify(tipoDocumentoRepository).deleteById("tdoc-1");
    }
}
