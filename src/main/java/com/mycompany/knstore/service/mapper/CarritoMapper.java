package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.CuentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Carrito} and its DTO {@link CarritoDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarritoMapper extends EntityMapper<CarritoDTO, Carrito> {
    @Mapping(target = "cuenta", source = "cuenta", qualifiedByName = "cuentaId")
    CarritoDTO toDto(Carrito s);

    @Named("cuentaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CuentaDTO toDtoCuentaId(Cuenta cuenta);
}
