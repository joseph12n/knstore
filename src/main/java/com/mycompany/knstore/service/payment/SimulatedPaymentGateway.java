package com.mycompany.knstore.service.payment;

import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Pasarela simulada para desarrollo y pruebas. Aprueba siempre los pagos de
 * forma simbolica: genera la referencia y resuelve cualquier callback como
 * APPROVED con codigo de autorizacion. La pasarela real futura sera otra
 * implementacion de {@link PaymentGateway}.
 */
@Component
@ConditionalOnProperty(name = "knstore.payment.gateway.type", havingValue = "simulated", matchIfMissing = true)
public class SimulatedPaymentGateway implements PaymentGateway {

    private static final Logger LOG = LoggerFactory.getLogger(SimulatedPaymentGateway.class);

    @Override
    public String iniciarPago(BigDecimal monto) {
        String referencia = "SIM-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        LOG.debug("Pasarela simulada: pago iniciado con referencia {}", referencia);
        return referencia;
    }

    @Override
    public String consultarEstado(String referencia) {
        LOG.debug("Pasarela simulada: consultando estado de {}", referencia);
        return "APPROVED";
    }

    @Override
    public ResultadoCallback procesarCallback(CallbackPayload payload) {
        String codigo =
            payload.codigoAutorizacion() != null
                ? payload.codigoAutorizacion()
                : "AUT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LOG.debug("Pasarela simulada: callback resuelto como APPROVED");
        return new ResultadoCallback("APPROVED", codigo, "Pago aprobado por la pasarela");
    }

    @Override
    public void reembolsar(String referencia, BigDecimal monto, String motivo) {
        LOG.debug("Pasarela simulada: reembolso de {} por {} solicitado ({})", referencia, monto, motivo);
    }
}
