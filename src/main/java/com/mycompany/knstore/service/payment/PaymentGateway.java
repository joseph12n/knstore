package com.mycompany.knstore.service.payment;

import java.math.BigDecimal;

/**
 * Abstraccion de la pasarela de pagos. La pasarela concreta (simulada u otra)
 * se configura con la propiedad {@code knstore.payment.gateway.type}; el dominio
 * no depende de ningun proveedor especifico.
 */
public interface PaymentGateway {
    /**
     * Inicia un pago en la pasarela dejandolo en estado PENDING.
     *
     * @param monto monto a cobrar.
     * @return referencia generada por la pasarela.
     */
    String iniciarPago(BigDecimal monto);

    /**
     * Consulta el estado actual de una referencia en la pasarela.
     *
     * @param referencia referencia generada por {@link #iniciarPago(BigDecimal)}.
     * @return estado reportado por la pasarela.
     */
    String consultarEstado(String referencia);

    /**
     * Procesa un callback recibido desde la pasarela.
     *
     * @param payload payload del callback.
     * @return resultado resuelto de la transaccion.
     */
    ResultadoCallback procesarCallback(CallbackPayload payload);

    /**
     * Solicita el reembolso de una transaccion aprobada.
     *
     * @param referencia referencia de la transaccion.
     * @param monto monto a reembolsar.
     * @param motivo motivo del reembolso.
     */
    void reembolsar(String referencia, BigDecimal monto, String motivo);

    /**
     * Payload de un callback de la pasarela.
     *
     * @param referencia referencia de la transaccion.
     * @param estado estado reportado (APPROVED o REJECTED).
     * @param monto monto pagado.
     * @param codigoAutorizacion codigo de autorizacion (opcional).
     */
    record CallbackPayload(String referencia, String estado, BigDecimal monto, String codigoAutorizacion) {}

    /**
     * Resultado de procesar un callback.
     *
     * @param estado estado resuelto (APPROVED o REJECTED).
     * @param codigoAutorizacion codigo de autorizacion si fue aprobado.
     * @param descripcion descripcion legible del resultado.
     */
    record ResultadoCallback(String estado, String codigoAutorizacion, String descripcion) {}
}
