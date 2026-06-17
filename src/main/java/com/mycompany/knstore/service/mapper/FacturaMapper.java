package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Factura} and its DTO {@link FacturaDTO}.
 */
@Mapper(componentModel = "spring")
public interface FacturaMapper extends EntityMapper<FacturaDTO, Factura> {
    @Mapping(target = "pago", source = "pago", qualifiedByName = "pagoId")
    FacturaDTO toDto(Factura s);

    @Named("pagoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PagoDTO toDtoPagoId(Pago pago);
}
