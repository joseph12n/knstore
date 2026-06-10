package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.DireccionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Direccion} and its DTO {@link DireccionDTO}.
 */
@Mapper(componentModel = "spring")
public interface DireccionMapper extends EntityMapper<DireccionDTO, Direccion> {
    @Mapping(target = "cuenta", source = "cuenta", qualifiedByName = "cuentaId")
    DireccionDTO toDto(Direccion s);

    @Named("cuentaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CuentaDTO toDtoCuentaId(Cuenta cuenta);
}
