package com.mycompany.knstore.service.payment;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.service.dto.PagoCallbackRequestDTO;
import com.mycompany.knstore.service.util.MoneyUtils;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "knstore.payment.gateway", name = "type", havingValue = "simulated", matchIfMissing = true)
public class SimulatedPaymentGateway implements PaymentGateway {

    private final EstadoPago defaultCallbackResult;

    public SimulatedPaymentGateway(@Value("${knstore.payment.gateway.simulated-result:APPROVED}") String defaultCallbackResult) {
        this.defaultCallbackResult = parseSupportedStatus(defaultCallbackResult, EstadoPago.APPROVED);
    }

    @Override
    public PaymentGatewayInitResult iniciarPago(Pedido pedido, MetodoPago metodoPago) {
        String referencia = "SIM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(Locale.ROOT);
        return new PaymentGatewayInitResult(referencia, "Pago iniciado en pasarela simulada");
    }

    @Override
    public PaymentGatewayStatusResult consultarEstado(String referenciaPasarela) {
        return new PaymentGatewayStatusResult(referenciaPasarela, defaultCallbackResult, null, null, "Consulta simulada de estado");
    }

    @Override
    public PaymentGatewayStatusResult procesarCallback(PagoCallbackRequestDTO callbackRequest) {
        EstadoPago resolvedStatus =
            callbackRequest.getEstado() != null
                ? parseSupportedStatus(callbackRequest.getEstado().name(), defaultCallbackResult)
                : defaultCallbackResult;

        return new PaymentGatewayStatusResult(
            callbackRequest.getReferenciaPasarela(),
            resolvedStatus,
            MoneyUtils.normalizeOrZero(callbackRequest.getMonto()),
            callbackRequest.getCodigoAutorizacion(),
            callbackRequest.getDescripcionRespuesta() != null ? callbackRequest.getDescripcionRespuesta() : "Callback simulado procesado"
        );
    }

    @Override
    public PaymentGatewayRefundResult reembolsar(Pago pago, String motivo) {
        return new PaymentGatewayRefundResult(pago.getReferenciaPasarela(), "Reembolso simulado: " + motivo);
    }

    private EstadoPago parseSupportedStatus(String rawStatus, EstadoPago fallback) {
        EstadoPago parsed;
        try {
            parsed = EstadoPago.valueOf(rawStatus.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            return fallback;
        }

        if (parsed == EstadoPago.APPROVED || parsed == EstadoPago.REJECTED) {
            return parsed;
        }
        return fallback;
    }
}
