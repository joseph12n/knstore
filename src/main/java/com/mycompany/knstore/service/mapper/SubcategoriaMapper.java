package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.service.dto.CategoriaDTO;
import com.mycompany.knstore.service.dto.SubcategoriaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Subcategoria} and its DTO {@link SubcategoriaDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubcategoriaMapper extends EntityMapper<SubcategoriaDTO, Subcategoria> {
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaNombre")
    SubcategoriaDTO toDto(Subcategoria s);

    @Named("categoriaNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "activo", source = "activo")
    CategoriaDTO toDtoCategoriaNombre(Categoria categoria);
}
