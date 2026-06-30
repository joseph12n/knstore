package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pago} and its DTO {@link PagoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PagoMapper extends EntityMapper<PagoDTO, Pago> {
    @Mapping(target = "pedido", source = "pedido", qualifiedByName = "pedidoId")
    PagoDTO toDto(Pago s);

    @Named("pedidoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PedidoDTO toDtoPedidoId(Pedido pedido);
}
