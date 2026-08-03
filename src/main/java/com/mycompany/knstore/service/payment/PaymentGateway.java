package com.mycompany.knstore.service.payment;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.service.dto.PagoCallbackRequestDTO;

public interface PaymentGateway {
    PaymentGatewayInitResult iniciarPago(Pedido pedido, MetodoPago metodoPago);

    PaymentGatewayStatusResult consultarEstado(String referenciaPasarela);

    PaymentGatewayStatusResult procesarCallback(PagoCallbackRequestDTO callbackRequest);

    PaymentGatewayRefundResult reembolsar(Pago pago, String motivo);
}
