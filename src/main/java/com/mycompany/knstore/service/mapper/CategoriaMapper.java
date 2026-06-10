package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.service.dto.CategoriaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Categoria} and its DTO {@link CategoriaDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoriaMapper extends EntityMapper<CategoriaDTO, Categoria> {}
