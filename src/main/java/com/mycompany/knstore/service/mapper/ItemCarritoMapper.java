package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.VarianteProducto;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.dto.VarianteProductoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ItemCarrito} and its DTO {@link ItemCarritoDTO}.
 */
@Mapper(componentModel = "spring")
public interface ItemCarritoMapper extends EntityMapper<ItemCarritoDTO, ItemCarrito> {
    @Mapping(target = "carrito", source = "carrito", qualifiedByName = "carritoId")
    @Mapping(target = "producto", source = "producto", qualifiedByName = "productoNombre")
    @Mapping(target = "variante", source = "variante", qualifiedByName = "varianteProductoSku")
    ItemCarritoDTO toDto(ItemCarrito s);

    @Named("carritoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CarritoDTO toDtoCarritoId(Carrito carrito);

    @Named("productoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ProductoDTO toDtoProductoNombre(Producto producto);

    @Named("varianteProductoSku")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", source = "sku")
    VarianteProductoDTO toDtoVarianteProductoSku(VarianteProducto varianteProducto);
}
