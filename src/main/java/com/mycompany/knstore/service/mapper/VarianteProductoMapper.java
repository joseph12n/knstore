package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.VarianteProducto;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.dto.VarianteProductoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VarianteProducto} and its DTO {@link VarianteProductoDTO}.
 */
@Mapper(componentModel = "spring")
public interface VarianteProductoMapper extends EntityMapper<VarianteProductoDTO, VarianteProducto> {
    @Mapping(target = "producto", source = "producto", qualifiedByName = "productoNombre")
    VarianteProductoDTO toDto(VarianteProducto s);

    @Named("productoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ProductoDTO toDtoProductoNombre(Producto producto);
}
