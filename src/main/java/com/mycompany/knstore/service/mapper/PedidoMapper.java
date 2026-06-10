package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pedido} and its DTO {@link PedidoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PedidoMapper extends EntityMapper<PedidoDTO, Pedido> {
    @Mapping(target = "direccion", source = "direccion", qualifiedByName = "direccionId")
    @Mapping(target = "envio", source = "envio", qualifiedByName = "envioId")
    @Mapping(target = "cuenta", source = "cuenta", qualifiedByName = "cuentaId")
    PedidoDTO toDto(Pedido s);

    @Named("direccionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DireccionDTO toDtoDireccionId(Direccion direccion);

    @Named("envioId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EnvioDTO toDtoEnvioId(Envio envio);

    @Named("cuentaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CuentaDTO toDtoCuentaId(Cuenta cuenta);
}
