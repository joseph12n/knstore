package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.repository.MarcaRepository;
import com.mycompany.knstore.service.dto.MarcaDTO;
import com.mycompany.knstore.service.mapper.MarcaMapper;
import com.mycompany.knstore.service.mapper.MarcaMapperImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarcaServiceImplTest {

    private final MarcaMapper marcaMapper = new MarcaMapperImpl();

    @Mock
    private MarcaRepository marcaRepository;

    private MarcaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MarcaServiceImpl(marcaRepository, marcaMapper);
    }

    @Test
    void saveMapeaYGuardaLaMarcaRetornandoElDTOPersistido() {
        MarcaDTO dto = new MarcaDTO();
        dto.setNombre("Nike");
        dto.setSlug("nike");
        when(marcaRepository.save(any())).thenAnswer(invocation -> {
            Marca guardada = invocation.getArgument(0);
            guardada.setId("marca-1");
            return guardada;
        });

        MarcaDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo("marca-1");
        assertThat(result.getNombre()).isEqualTo("Nike");
        assertThat(result.getSlug()).isEqualTo("nike");
        verify(marcaRepository).save(any());
    }

    @Test
    void updateMapeaYGuardaLaMarcaConSusDatosActualizados() {
        MarcaDTO dto = new MarcaDTO();
        dto.setId("marca-1");
        dto.setNombre("Adidas");
        dto.setSlug("adidas");
        when(marcaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MarcaDTO result = service.update(dto);

        assertThat(result.getId()).isEqualTo("marca-1");
        assertThat(result.getNombre()).isEqualTo("Adidas");
        assertThat(result.getSlug()).isEqualTo("adidas");
    }

    @Test
    void partialUpdateActualizaSoloCamposNoNulos() {
        Marca existente = new Marca();
        existente.setId("marca-1");
        existente.setNombre("Nike");
        existente.setSlug("nike");
        when(marcaRepository.findById("marca-1")).thenReturn(Optional.of(existente));
        when(marcaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MarcaDTO cambios = new MarcaDTO();
        cambios.setId("marca-1");
        cambios.setSlug("nike-oficial");

        Optional<MarcaDTO> result = service.partialUpdate(cambios);

        assertThat(result).isPresent();
        assertThat(result.get().getSlug()).isEqualTo("nike-oficial");
        assertThat(result.get().getNombre()).isEqualTo("Nike");
    }

    @Test
    void partialUpdateCuandoNoExisteLaMarcaRetornaVacioYNoGuarda() {
        when(marcaRepository.findById("marca-404")).thenReturn(Optional.empty());

        MarcaDTO cambios = new MarcaDTO();
        cambios.setId("marca-404");
        cambios.setNombre("Inexistente");

        Optional<MarcaDTO> result = service.partialUpdate(cambios);

        assertThat(result).isEmpty();
        verify(marcaRepository, never()).save(any());
    }

    @Test
    void findOneCuandoExisteRetornaElDTOMapeado() {
        Marca marca = new Marca();
        marca.setId("marca-1");
        marca.setNombre("Nike");
        marca.setSlug("nike");
        when(marcaRepository.findById("marca-1")).thenReturn(Optional.of(marca));

        Optional<MarcaDTO> result = service.findOne("marca-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("marca-1");
        assertThat(result.get().getNombre()).isEqualTo("Nike");
        assertThat(result.get().getSlug()).isEqualTo("nike");
    }

    @Test
    void findOneCuandoNoExisteRetornaVacio() {
        when(marcaRepository.findById("marca-404")).thenReturn(Optional.empty());

        Optional<MarcaDTO> result = service.findOne("marca-404");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaLaListaDeMarcasMapeadasADTO() {
        Marca nike = new Marca();
        nike.setId("marca-1");
        nike.setNombre("Nike");
        nike.setSlug("nike");
        Marca adidas = new Marca();
        adidas.setId("marca-2");
        adidas.setNombre("Adidas");
        adidas.setSlug("adidas");
        when(marcaRepository.findAll()).thenReturn(List.of(nike, adidas));

        List<MarcaDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(MarcaDTO::getNombre).containsExactly("Nike", "Adidas");
        assertThat(result).extracting(MarcaDTO::getSlug).containsExactly("nike", "adidas");
    }

    @Test
    void deleteEliminaLaMarcaPorId() {
        service.delete("marca-1");

        verify(marcaRepository).deleteById("marca-1");
    }
}
