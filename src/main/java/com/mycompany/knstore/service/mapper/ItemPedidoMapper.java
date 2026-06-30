package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ItemPedido} and its DTO {@link ItemPedidoDTO}.
 */
@Mapper(componentModel = "spring")
public interface ItemPedidoMapper extends EntityMapper<ItemPedidoDTO, ItemPedido> {
    @Mapping(target = "pedido", source = "pedido", qualifiedByName = "pedidoId")
    @Mapping(target = "producto", source = "producto", qualifiedByName = "productoNombre")
    ItemPedidoDTO toDto(ItemPedido s);

    @Named("pedidoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PedidoDTO toDtoPedidoId(Pedido pedido);

    @Named("productoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ProductoDTO toDtoProductoNombre(Producto producto);
}
