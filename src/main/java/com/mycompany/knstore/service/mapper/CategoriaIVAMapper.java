package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.service.dto.CategoriaIVADTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CategoriaIVA} and its DTO {@link CategoriaIVADTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoriaIVAMapper extends EntityMapper<CategoriaIVADTO, CategoriaIVA> {}
