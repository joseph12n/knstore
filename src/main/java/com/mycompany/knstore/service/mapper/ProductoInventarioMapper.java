package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.service.dto.ProductoInventarioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductoInventario} and its DTO {@link ProductoInventarioDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductoInventarioMapper extends EntityMapper<ProductoInventarioDTO, ProductoInventario> {}
