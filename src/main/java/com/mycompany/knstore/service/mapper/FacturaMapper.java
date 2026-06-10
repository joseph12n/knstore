package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Factura} and its DTO {@link FacturaDTO}.
 */
@Mapper(componentModel = "spring")
public interface FacturaMapper extends EntityMapper<FacturaDTO, Factura> {
    @Mapping(target = "pedido", source = "pedido", qualifiedByName = "pedidoId")
    FacturaDTO toDto(Factura s);

    @Named("pedidoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PedidoDTO toDtoPedidoId(Pedido pedido);
}
