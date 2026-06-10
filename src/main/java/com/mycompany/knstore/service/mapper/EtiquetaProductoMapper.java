package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.EtiquetaProducto;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.service.dto.EtiquetaProductoDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EtiquetaProducto} and its DTO {@link EtiquetaProductoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EtiquetaProductoMapper extends EntityMapper<EtiquetaProductoDTO, EtiquetaProducto> {
    @Mapping(target = "producto", source = "producto", qualifiedByName = "productoNombre")
    EtiquetaProductoDTO toDto(EtiquetaProducto s);

    @Named("productoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ProductoDTO toDtoProductoNombre(Producto producto);
}
