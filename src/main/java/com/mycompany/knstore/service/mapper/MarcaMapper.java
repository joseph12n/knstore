package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.service.dto.MarcaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Marca} and its DTO {@link MarcaDTO}.
 */
@Mapper(componentModel = "spring")
public interface MarcaMapper extends EntityMapper<MarcaDTO, Marca> {}
