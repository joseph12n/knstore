package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductoPrecio} and its DTO {@link ProductoPrecioDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductoPrecioMapper extends EntityMapper<ProductoPrecioDTO, ProductoPrecio> {}
