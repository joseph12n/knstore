package com.mycompany.knstore.service.payment;

import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Pasarela simulada para desarrollo y pruebas. Genera la referencia del pago,
 * deja la transaccion en PENDING y la resuelve por callback. El resultado por
 * defecto se puede forzar con {@code knstore.payment.gateway.simulated.result}
 * (approve o reject); si no se configura, se usa el estado del payload.
 */
@Component
@ConditionalOnProperty(name = "knstore.payment.gateway.type", havingValue = "simulated", matchIfMissing = true)
public class SimulatedPaymentGateway implements PaymentGateway {

    private static final Logger LOG = LoggerFactory.getLogger(SimulatedPaymentGateway.class);

    private final String resultadoForzado;

    public SimulatedPaymentGateway(@Value("${knstore.payment.gateway.simulated.result:}") String resultadoForzado) {
        this.resultadoForzado = resultadoForzado;
    }

    @Override
    public String iniciarPago(BigDecimal monto) {
        String referencia = "SIM-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        LOG.debug("Pasarela simulada: pago iniciado con referencia {}", referencia);
        return referencia;
    }

    @Override
    public String consultarEstado(String referencia) {
        LOG.debug("Pasarela simulada: consultando estado de {}", referencia);
        return "PENDING";
    }

    @Override
    public ResultadoCallback procesarCallback(CallbackPayload payload) {
        String estado = resolverEstado(payload.estado());
        String codigo = null;
        String descripcion;
        if ("APPROVED".equals(estado)) {
            codigo =
                payload.codigoAutorizacion() != null
                    ? payload.codigoAutorizacion()
                    : "AUT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            descripcion = "Pago aprobado por la pasarela";
        } else {
            descripcion = "Pago rechazado por la pasarela";
        }
        LOG.debug("Pasarela simulada: callback resuelto como {}", estado);
        return new ResultadoCallback(estado, codigo, descripcion);
    }

    @Override
    public void reembolsar(String referencia, BigDecimal monto, String motivo) {
        LOG.debug("Pasarela simulada: reembolso de {} por {} solicitado ({})", referencia, monto, motivo);
    }

    private String resolverEstado(String estadoPayload) {
        if (resultadoForzado != null && !resultadoForzado.isBlank()) {
            return "reject".equalsIgnoreCase(resultadoForzado) ? "REJECTED" : "APPROVED";
        }
        return "APPROVED".equalsIgnoreCase(estadoPayload) ? "APPROVED" : "REJECTED";
    }
}
