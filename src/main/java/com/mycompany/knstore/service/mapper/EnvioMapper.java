package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.service.dto.EnvioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Envio} and its DTO {@link EnvioDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnvioMapper extends EntityMapper<EnvioDTO, Envio> {}
