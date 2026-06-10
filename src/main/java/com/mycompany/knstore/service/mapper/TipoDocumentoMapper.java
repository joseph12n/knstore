package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.TipoDocumento;
import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TipoDocumento} and its DTO {@link TipoDocumentoDTO}.
 */
@Mapper(componentModel = "spring")
public interface TipoDocumentoMapper extends EntityMapper<TipoDocumentoDTO, TipoDocumento> {}
