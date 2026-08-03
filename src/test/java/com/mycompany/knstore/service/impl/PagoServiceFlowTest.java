package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.domain.enumeration.MetodoPago;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.PagoCallbackRequestDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.PagoIniciarRequestDTO;
import com.mycompany.knstore.service.mapper.PagoMapper;
import com.mycompany.knstore.service.payment.PaymentGateway;
import com.mycompany.knstore.service.payment.PaymentGatewayInitResult;
import com.mycompany.knstore.service.payment.PaymentGatewayRefundResult;
import com.mycompany.knstore.service.payment.PaymentGatewayStatusResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PagoServiceFlowTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private PagoMapper pagoMapper;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    void iniciarPagoCreaPagoPendienteConReferencia() {
        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        pedido.setEstado(EstadoPedido.PENDING);
        pedido.setTotal(new BigDecimal("120.00"));

        PagoIniciarRequestDTO request = new PagoIniciarRequestDTO();
        request.setPedidoId("pedido-1");
        request.setMetodoPago(MetodoPago.PSE);

        when(pedidoRepository.findById("pedido-1")).thenReturn(Optional.of(pedido));
        when(pagoRepository.findByPedidoId("pedido-1", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of()));
        when(paymentGateway.iniciarPago(pedido, MetodoPago.PSE)).thenReturn(new PaymentGatewayInitResult("SIM-ABC123", "ok"));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toDto(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            PagoDTO dto = new PagoDTO();
            dto.setEstado(pago.getEstado());
            dto.setReferenciaPasarela(pago.getReferenciaPasarela());
            dto.setMonto(pago.getMonto());
            return dto;
        });

        PagoDTO result = pagoService.iniciarPago(request);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.PENDING);
        assertThat(result.getReferenciaPasarela()).isEqualTo("SIM-ABC123");
        assertThat(result.getMonto()).isEqualByComparingTo("120.00");
    }

    @Test
    void procesarCallbackAprobadoConfirmaPedido() {
        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        pedido.setEstado(EstadoPedido.PENDING);
        pedido.setTotal(new BigDecimal("50.00"));

        Pago pago = new Pago();
        pago.setId("pago-1");
        pago.setEstado(EstadoPago.PENDING);
        pago.setReferenciaPasarela("SIM-REF");
        pago.setPedido(pedido);
        pago.setIntentos(0);

        PagoCallbackRequestDTO callback = new PagoCallbackRequestDTO();
        callback.setReferenciaPasarela("SIM-REF");
        callback.setMonto(new BigDecimal("50.00"));

        when(paymentGateway.procesarCallback(callback)).thenReturn(
            new PaymentGatewayStatusResult("SIM-REF", EstadoPago.APPROVED, new BigDecimal("50.00"), "AUTH-1", "approved")
        );
        when(pagoRepository.findByReferenciaPasarela("SIM-REF")).thenReturn(Optional.of(pago));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toDto(any(Pago.class))).thenAnswer(invocation -> {
            Pago saved = invocation.getArgument(0);
            PagoDTO dto = new PagoDTO();
            dto.setEstado(saved.getEstado());
            return dto;
        });

        PagoDTO result = pagoService.procesarCallback(callback);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMED);
    }

    @Test
    void procesarCallbackConMontoIncoherenteRechazaYCancelaPedido() {
        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        pedido.setEstado(EstadoPedido.PENDING);
        pedido.setTotal(new BigDecimal("75.00"));

        Pago pago = new Pago();
        pago.setId("pago-1");
        pago.setEstado(EstadoPago.PENDING);
        pago.setReferenciaPasarela("SIM-REF");
        pago.setPedido(pedido);

        PagoCallbackRequestDTO callback = new PagoCallbackRequestDTO();
        callback.setReferenciaPasarela("SIM-REF");
        callback.setMonto(new BigDecimal("70.00"));

        when(paymentGateway.procesarCallback(callback)).thenReturn(
            new PaymentGatewayStatusResult("SIM-REF", EstadoPago.APPROVED, new BigDecimal("70.00"), null, null)
        );
        when(pagoRepository.findByReferenciaPasarela("SIM-REF")).thenReturn(Optional.of(pago));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toDto(any(Pago.class))).thenAnswer(invocation -> {
            Pago saved = invocation.getArgument(0);
            PagoDTO dto = new PagoDTO();
            dto.setEstado(saved.getEstado());
            return dto;
        });

        PagoDTO result = pagoService.procesarCallback(callback);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.REJECTED);
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CANCELLED);
    }

    @Test
    void callbackDuplicadoNoReprocesaPagoNoPendiente() {
        Pago pago = new Pago();
        pago.setId("pago-1");
        pago.setEstado(EstadoPago.APPROVED);
        pago.setReferenciaPasarela("SIM-REF");

        PagoCallbackRequestDTO callback = new PagoCallbackRequestDTO();
        callback.setReferenciaPasarela("SIM-REF");
        callback.setMonto(new BigDecimal("10.00"));

        when(paymentGateway.procesarCallback(callback)).thenReturn(
            new PaymentGatewayStatusResult("SIM-REF", EstadoPago.APPROVED, new BigDecimal("10.00"), null, null)
        );
        when(pagoRepository.findByReferenciaPasarela("SIM-REF")).thenReturn(Optional.of(pago));
        when(pagoMapper.toDto(any(Pago.class))).thenReturn(new PagoDTO());

        pagoService.procesarCallback(callback);

        verify(pagoRepository, never()).save(any(Pago.class));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void reembolsarSoloPermitePagosAprobados() {
        Pago pago = new Pago();
        pago.setId("pago-1");
        pago.setEstado(EstadoPago.APPROVED);
        pago.setReferenciaPasarela("SIM-REF");

        when(pagoRepository.findById("pago-1")).thenReturn(Optional.of(pago));
        when(paymentGateway.reembolsar(pago, "Cliente solicito devolucion")).thenReturn(
            new PaymentGatewayRefundResult("SIM-REF", "reembolso ok")
        );
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toDto(any(Pago.class))).thenReturn(new PagoDTO());

        pagoService.reembolsar("pago-1", "Cliente solicito devolucion");

        assertThat(pago.getEstado()).isEqualTo(EstadoPago.REFUNDED);

        Pago noAprobado = new Pago();
        noAprobado.setId("pago-2");
        noAprobado.setEstado(EstadoPago.PENDING);
        when(pagoRepository.findById("pago-2")).thenReturn(Optional.of(noAprobado));

        assertThrows(IllegalArgumentException.class, () -> pagoService.reembolsar("pago-2", "x"));
    }
}
