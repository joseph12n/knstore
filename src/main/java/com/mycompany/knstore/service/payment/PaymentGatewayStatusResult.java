package com.mycompany.knstore.service.payment;

import com.mycompany.knstore.domain.enumeration.EstadoPago;
import java.math.BigDecimal;

public record PaymentGatewayStatusResult(
    String referenciaPasarela,
    EstadoPago estado,
    BigDecimal monto,
    String codigoAutorizacion,
    String descripcion
) {}
