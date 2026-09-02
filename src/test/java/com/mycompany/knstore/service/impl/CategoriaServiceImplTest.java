package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.repository.CategoriaRepository;
import com.mycompany.knstore.service.dto.CategoriaDTO;
import com.mycompany.knstore.service.mapper.CategoriaMapper;
import com.mycompany.knstore.service.mapper.CategoriaMapperImpl;
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
class CategoriaServiceImplTest {

    private final CategoriaMapper categoriaMapper = new CategoriaMapperImpl();

    @Mock
    private CategoriaRepository categoriaRepository;

    private CategoriaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CategoriaServiceImpl(categoriaRepository, categoriaMapper);
    }

    @Test
    void saveMapeaYGuardaLaCategoriaRetornandoElDTOPersistido() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Deportivos");
        dto.setSlug("deportivos");
        dto.setDescripcion("Zapatos para correr");
        dto.setActivo(true);
        when(categoriaRepository.save(any())).thenAnswer(invocation -> {
            Categoria guardada = invocation.getArgument(0);
            guardada.setId("cat-1");
            return guardada;
        });

        CategoriaDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo("cat-1");
        assertThat(result.getNombre()).isEqualTo("Deportivos");
        assertThat(result.getSlug()).isEqualTo("deportivos");
        assertThat(result.getDescripcion()).isEqualTo("Zapatos para correr");
        assertThat(result.getActivo()).isTrue();
        verify(categoriaRepository).save(any());
    }

    @Test
    void updateMapeaYGuardaLaCategoriaConSusDatosActualizados() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId("cat-1");
        dto.setNombre("Formales");
        dto.setSlug("formales");
        dto.setActivo(false);
        when(categoriaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaDTO result = service.update(dto);

        assertThat(result.getId()).isEqualTo("cat-1");
        assertThat(result.getNombre()).isEqualTo("Formales");
        assertThat(result.getSlug()).isEqualTo("formales");
        assertThat(result.getActivo()).isFalse();
    }

    @Test
    void partialUpdateActualizaSoloCamposNoNulos() {
        Categoria existente = new Categoria();
        existente.setId("cat-1");
        existente.setNombre("Deportivos");
        existente.setSlug("deportivos");
        existente.setDescripcion("Descripcion vieja");
        existente.setActivo(true);
        when(categoriaRepository.findById("cat-1")).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaDTO cambios = new CategoriaDTO();
        cambios.setId("cat-1");
        cambios.setDescripcion("Descripcion nueva");

        Optional<CategoriaDTO> result = service.partialUpdate(cambios);

        assertThat(result).isPresent();
        assertThat(result.get().getDescripcion()).isEqualTo("Descripcion nueva");
        assertThat(result.get().getNombre()).isEqualTo("Deportivos");
        assertThat(result.get().getSlug()).isEqualTo("deportivos");
        assertThat(result.get().getActivo()).isTrue();
    }

    @Test
    void partialUpdateCuandoNoExisteLaCategoriaRetornaVacioYNoGuarda() {
        when(categoriaRepository.findById("cat-404")).thenReturn(Optional.empty());

        CategoriaDTO cambios = new CategoriaDTO();
        cambios.setId("cat-404");
        cambios.setNombre("Inexistente");

        Optional<CategoriaDTO> result = service.partialUpdate(cambios);

        assertThat(result).isEmpty();
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void findOneCuandoExisteRetornaElDTOMapeado() {
        Categoria categoria = new Categoria();
        categoria.setId("cat-1");
        categoria.setNombre("Deportivos");
        categoria.setSlug("deportivos");
        categoria.setActivo(true);
        when(categoriaRepository.findById("cat-1")).thenReturn(Optional.of(categoria));

        Optional<CategoriaDTO> result = service.findOne("cat-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("cat-1");
        assertThat(result.get().getNombre()).isEqualTo("Deportivos");
        assertThat(result.get().getActivo()).isTrue();
    }

    @Test
    void findOneCuandoNoExisteRetornaVacio() {
        when(categoriaRepository.findById("cat-404")).thenReturn(Optional.empty());

        Optional<CategoriaDTO> result = service.findOne("cat-404");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaLaPaginaDeCategoriasMapeadasADTO() {
        Categoria primera = new Categoria();
        primera.setId("cat-1");
        primera.setNombre("Deportivos");
        Categoria segunda = new Categoria();
        segunda.setId("cat-2");
        segunda.setNombre("Formales");
        when(categoriaRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(primera, segunda)));

        Page<CategoriaDTO> result = service.findAll(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(CategoriaDTO::getNombre).containsExactly("Deportivos", "Formales");
    }

    @Test
    void deleteEliminaLaCategoriaPorId() {
        service.delete("cat-1");

        verify(categoriaRepository).deleteById("cat-1");
    }
}
