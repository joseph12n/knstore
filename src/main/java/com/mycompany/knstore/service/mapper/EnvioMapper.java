package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Envio} and its DTO {@link EnvioDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnvioMapper extends EntityMapper<EnvioDTO, Envio> {
    @Mapping(target = "pedido", source = "pedido", qualifiedByName = "pedidoId")
    EnvioDTO toDto(Envio s);

    @Named("pedidoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PedidoDTO toDtoPedidoId(Pedido pedido);
}
