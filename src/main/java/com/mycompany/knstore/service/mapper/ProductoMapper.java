package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.dto.SubcategoriaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Producto} and its DTO {@link ProductoDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductoMapper extends EntityMapper<ProductoDTO, Producto> {
    @Mapping(target = "subcategoria", source = "subcategoria", qualifiedByName = "subcategoriaNombre")
    ProductoDTO toDto(Producto s);

    @Named("subcategoriaNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    SubcategoriaDTO toDtoSubcategoriaNombre(Subcategoria subcategoria);
}
