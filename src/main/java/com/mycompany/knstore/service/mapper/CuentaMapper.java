package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.TipoDocumento;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import com.mycompany.knstore.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cuenta} and its DTO {@link CuentaDTO}.
 */
@Mapper(componentModel = "spring")
public interface CuentaMapper extends EntityMapper<CuentaDTO, Cuenta> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "tipoDocumento", source = "tipoDocumento", qualifiedByName = "tipoDocumentoSigla")
    CuentaDTO toDto(Cuenta s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("tipoDocumentoSigla")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sigla", source = "sigla")
    TipoDocumentoDTO toDtoTipoDocumentoSigla(TipoDocumento tipoDocumento);
}
