package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoImagen;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.dto.ProductoImagenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductoImagen} and its DTO {@link ProductoImagenDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductoImagenMapper extends EntityMapper<ProductoImagenDTO, ProductoImagen> {
    @Mapping(target = "producto", source = "producto", qualifiedByName = "productoId")
    ProductoImagenDTO toDto(ProductoImagen s);

    @Named("productoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductoDTO toDtoProductoId(Producto producto);
}
