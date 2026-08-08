package com.mycompany.knstore.service.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimulatedPaymentGatewayTest {

    @Test
    void iniciarPagoGeneraReferenciaConPrefijo() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway("");
        String referencia = gateway.iniciarPago(new BigDecimal("100000.00"));
        assertThat(referencia).startsWith("SIM-");
    }

    @Test
    void callbackAprobadoDevuelveCodigoDeAutorizacion() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway("");
        PaymentGateway.ResultadoCallback resultado = gateway.procesarCallback(
            new PaymentGateway.CallbackPayload("SIM-1", "APPROVED", new BigDecimal("100000.00"), null)
        );
        assertThat(resultado.estado()).isEqualTo("APPROVED");
        assertThat(resultado.codigoAutorizacion()).startsWith("AUT-");
    }

    @Test
    void callbackRechazadoSinCodigoDeAutorizacion() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway("");
        PaymentGateway.ResultadoCallback resultado = gateway.procesarCallback(
            new PaymentGateway.CallbackPayload("SIM-1", "REJECTED", new BigDecimal("100000.00"), null)
        );
        assertThat(resultado.estado()).isEqualTo("REJECTED");
        assertThat(resultado.codigoAutorizacion()).isNull();
    }

    @Test
    void resultadoForzadoRejectIgnoraElEstadoDelPayload() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway("reject");
        PaymentGateway.ResultadoCallback resultado = gateway.procesarCallback(
            new PaymentGateway.CallbackPayload("SIM-1", "APPROVED", new BigDecimal("100000.00"), null)
        );
        assertThat(resultado.estado()).isEqualTo("REJECTED");
    }

    @Test
    void resultadoForzadoApproveApruebaSiempre() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway("approve");
        PaymentGateway.ResultadoCallback resultado = gateway.procesarCallback(
            new PaymentGateway.CallbackPayload("SIM-1", "REJECTED", new BigDecimal("100000.00"), null)
        );
        assertThat(resultado.estado()).isEqualTo("APPROVED");
    }
}
