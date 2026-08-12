package com.mycompany.knstore.service.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimulatedPaymentGatewayTest {

    @Test
    void iniciarPagoGeneraReferenciaConPrefijo() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();
        String referencia = gateway.iniciarPago(new BigDecimal("100000.00"));
        assertThat(referencia).startsWith("SIM-");
    }

    @Test
    void callbackApruebaSiempreConCodigoDeAutorizacion() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();
        PaymentGateway.ResultadoCallback resultado = gateway.procesarCallback(
            new PaymentGateway.CallbackPayload("SIM-1", "APPROVED", new BigDecimal("100000.00"), null)
        );
        assertThat(resultado.estado()).isEqualTo("APPROVED");
        assertThat(resultado.codigoAutorizacion()).startsWith("AUT-");
        assertThat(resultado.descripcion()).isNotBlank();
    }

    @Test
    void callbackConEstadoRechazadoTambienAprueba() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();
        PaymentGateway.ResultadoCallback resultado = gateway.procesarCallback(
            new PaymentGateway.CallbackPayload("SIM-1", "REJECTED", new BigDecimal("100000.00"), null)
        );
        assertThat(resultado.estado()).isEqualTo("APPROVED");
        assertThat(resultado.codigoAutorizacion()).startsWith("AUT-");
    }

    @Test
    void callbackConservaElCodigoDeAutorizacionDelPayload() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();
        PaymentGateway.ResultadoCallback resultado = gateway.procesarCallback(
            new PaymentGateway.CallbackPayload("SIM-1", "APPROVED", new BigDecimal("100000.00"), "AUT-EXTERNO")
        );
        assertThat(resultado.estado()).isEqualTo("APPROVED");
        assertThat(resultado.codigoAutorizacion()).isEqualTo("AUT-EXTERNO");
    }

    @Test
    void consultarEstadoSiempreDevuelveAprobado() {
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();
        assertThat(gateway.consultarEstado("SIM-1")).isEqualTo("APPROVED");
    }
}
