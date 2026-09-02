package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.repository.SubcategoriaRepository;
import com.mycompany.knstore.service.dto.CategoriaDTO;
import com.mycompany.knstore.service.dto.SubcategoriaDTO;
import com.mycompany.knstore.service.mapper.SubcategoriaMapper;
import com.mycompany.knstore.service.mapper.SubcategoriaMapperImpl;
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
class SubcategoriaServiceImplTest {

    private final SubcategoriaMapper subcategoriaMapper = new SubcategoriaMapperImpl();

    @Mock
    private SubcategoriaRepository subcategoriaRepository;

    private SubcategoriaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SubcategoriaServiceImpl(subcategoriaRepository, subcategoriaMapper);
    }

    @Test
    void saveMapeaYGuardaLaSubcategoriaConSuCategoria() {
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId("cat-1");
        categoriaDTO.setNombre("Deportivos");
        categoriaDTO.setSlug("deportivos");
        categoriaDTO.setActivo(true);
        SubcategoriaDTO dto = new SubcategoriaDTO();
        dto.setNombre("Tenis");
        dto.setSlug("tenis");
        dto.setDescripcion("Tenis deportivos");
        dto.setActivo(true);
        dto.setCategoria(categoriaDTO);
        when(subcategoriaRepository.save(any())).thenAnswer(invocation -> {
            Subcategoria guardada = invocation.getArgument(0);
            guardada.setId("sub-1");
            return guardada;
        });

        SubcategoriaDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo("sub-1");
        assertThat(result.getNombre()).isEqualTo("Tenis");
        assertThat(result.getSlug()).isEqualTo("tenis");
        assertThat(result.getActivo()).isTrue();
        assertThat(result.getCategoria().getId()).isEqualTo("cat-1");
        assertThat(result.getCategoria().getNombre()).isEqualTo("Deportivos");
    }

    @Test
    void updateMapeaYGuardaLaSubcategoriaConSusDatosActualizados() {
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId("cat-2");
        categoriaDTO.setNombre("Formales");
        categoriaDTO.setSlug("formales");
        categoriaDTO.setActivo(true);
        SubcategoriaDTO dto = new SubcategoriaDTO();
        dto.setId("sub-1");
        dto.setNombre("Zapato cuero");
        dto.setSlug("zapato-cuero");
        dto.setActivo(false);
        dto.setCategoria(categoriaDTO);
        when(subcategoriaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SubcategoriaDTO result = service.update(dto);

        assertThat(result.getId()).isEqualTo("sub-1");
        assertThat(result.getNombre()).isEqualTo("Zapato cuero");
        assertThat(result.getSlug()).isEqualTo("zapato-cuero");
        assertThat(result.getActivo()).isFalse();
        assertThat(result.getCategoria().getId()).isEqualTo("cat-2");
    }

    @Test
    void partialUpdateActualizaSoloCamposNoNulos() {
        Categoria categoria = new Categoria();
        categoria.setId("cat-1");
        categoria.setNombre("Deportivos");
        categoria.setSlug("deportivos");
        categoria.setActivo(true);
        Subcategoria existente = new Subcategoria();
        existente.setId("sub-1");
        existente.setNombre("Tenis");
        existente.setSlug("tenis");
        existente.setDescripcion("Descripcion vieja");
        existente.setActivo(true);
        existente.setCategoria(categoria);
        when(subcategoriaRepository.findById("sub-1")).thenReturn(Optional.of(existente));
        when(subcategoriaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SubcategoriaDTO cambios = new SubcategoriaDTO();
        cambios.setId("sub-1");
        cambios.setDescripcion("Descripcion nueva");

        Optional<SubcategoriaDTO> result = service.partialUpdate(cambios);

        assertThat(result).isPresent();
        assertThat(result.get().getDescripcion()).isEqualTo("Descripcion nueva");
        assertThat(result.get().getNombre()).isEqualTo("Tenis");
        assertThat(result.get().getSlug()).isEqualTo("tenis");
        assertThat(result.get().getActivo()).isTrue();
        assertThat(result.get().getCategoria().getId()).isEqualTo("cat-1");
    }

    @Test
    void partialUpdateCuandoNoExisteLaSubcategoriaRetornaVacioYNoGuarda() {
        when(subcategoriaRepository.findById("sub-404")).thenReturn(Optional.empty());

        SubcategoriaDTO cambios = new SubcategoriaDTO();
        cambios.setId("sub-404");
        cambios.setNombre("Inexistente");

        Optional<SubcategoriaDTO> result = service.partialUpdate(cambios);

        assertThat(result).isEmpty();
        verify(subcategoriaRepository, never()).save(any());
    }

    @Test
    void findOneUsaLaConsultaConRelacionesYRetornaElDTOMapeado() {
        Categoria categoria = new Categoria();
        categoria.setId("cat-1");
        categoria.setNombre("Deportivos");
        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setId("sub-1");
        subcategoria.setNombre("Tenis");
        subcategoria.setSlug("tenis");
        subcategoria.setActivo(true);
        subcategoria.setCategoria(categoria);
        when(subcategoriaRepository.findOneWithEagerRelationships("sub-1")).thenReturn(Optional.of(subcategoria));

        Optional<SubcategoriaDTO> result = service.findOne("sub-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("sub-1");
        assertThat(result.get().getNombre()).isEqualTo("Tenis");
        assertThat(result.get().getCategoria().getId()).isEqualTo("cat-1");
        assertThat(result.get().getCategoria().getNombre()).isEqualTo("Deportivos");
        verify(subcategoriaRepository).findOneWithEagerRelationships("sub-1");
    }

    @Test
    void findOneCuandoNoExisteRetornaVacio() {
        when(subcategoriaRepository.findOneWithEagerRelationships("sub-404")).thenReturn(Optional.empty());

        Optional<SubcategoriaDTO> result = service.findOne("sub-404");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllRetornaLaPaginaDeSubcategoriasMapeadasADTO() {
        Subcategoria primera = new Subcategoria();
        primera.setId("sub-1");
        primera.setNombre("Tenis");
        Subcategoria segunda = new Subcategoria();
        segunda.setId("sub-2");
        segunda.setNombre("Zapato cuero");
        when(subcategoriaRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(primera, segunda)));

        Page<SubcategoriaDTO> result = service.findAll(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(SubcategoriaDTO::getNombre).containsExactly("Tenis", "Zapato cuero");
    }

    @Test
    void findAllWithEagerRelationshipsRetornaLaPaginaMapeadaConSusCategorias() {
        Categoria categoria = new Categoria();
        categoria.setId("cat-1");
        categoria.setNombre("Deportivos");
        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setId("sub-1");
        subcategoria.setNombre("Tenis");
        subcategoria.setCategoria(categoria);
        when(subcategoriaRepository.findAllWithEagerRelationships(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(subcategoria)));

        Page<SubcategoriaDTO> result = service.findAllWithEagerRelationships(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNombre()).isEqualTo("Tenis");
        assertThat(result.getContent().get(0).getCategoria().getId()).isEqualTo("cat-1");
    }

    @Test
    void deleteEliminaLaSubcategoriaPorId() {
        service.delete("sub-1");

        verify(subcategoriaRepository).deleteById("sub-1");
    }
}
