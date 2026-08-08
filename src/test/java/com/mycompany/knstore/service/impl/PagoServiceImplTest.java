package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.MailService;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.invoice.FacturaPdfService;
import com.mycompany.knstore.service.mapper.PagoMapper;
import com.mycompany.knstore.service.mapper.PagoMapperImpl;
import com.mycompany.knstore.service.payment.PaymentGateway;
import com.mycompany.knstore.service.payment.SimulatedPaymentGateway;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    private final PagoMapper pagoMapper = new PagoMapperImpl();

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @Mock
    private FacturaPdfService facturaPdfService;

    @Mock
    private MailService mailService;

    private PagoServiceImpl service;

    private PaymentGateway paymentGateway;

    @BeforeEach
    void setUp() {
        paymentGateway = new SimulatedPaymentGateway("");
        service = new PagoServiceImpl(
            pagoRepository,
            pedidoRepository,
            cuentaRepository,
            facturaRepository,
            pagoMapper,
            historialEstadoService,
            paymentGateway,
            facturaPdfService,
            mailService
        );
    }

    private Pago pagoPendiente(String id, String referencia, BigDecimal monto, Pedido pedido) {
        Pago pago = new Pago();
        pago.setId(id);
        pago.setEstado(EstadoPago.PENDING);
        pago.setReferenciaPasarela(referencia);
        pago.setMonto(monto);
        pago.setPedido(pedido);
        return pago;
    }

    private Pedido pedidoPendiente() {
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setEstado(EstadoPedido.PENDING);
        pedido.setTotal(new BigDecimal("120000.00"));
        return pedido;
    }

    @Test
    void iniciarPagoApruebaAutomaticamenteYGeneraFactura() {
        Pedido pedido = pedidoPendiente();
        Pago[] guardado = new Pago[1];
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));
        when(pagoRepository.findByPedidoId(eq("p-1"), any(Pageable.class))).thenReturn(new PageImpl<>(java.util.List.of()));
        when(pagoRepository.save(any())).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            if (pago.getId() == null) {
                pago.setId("pg-1");
            }
            guardado[0] = pago;
            return pago;
        });
        when(pagoRepository.findByReferenciaPasarela(any())).thenAnswer(invocation -> Optional.ofNullable(guardado[0]));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(facturaRepository.findByPagoId(any(), any(Pageable.class))).thenReturn(Page.empty());
        when(facturaPdfService.generarConsecutivo("FE")).thenReturn("FE-000001");

        PagoDTO result = service.iniciarPago("p-1");

        assertThat(result.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(result.getReferenciaPasarela()).startsWith("SIM-");
        assertThat(result.getCodigoAutorizacion()).startsWith("AUT-");
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMED);
        verify(facturaPdfService).generarConsecutivo("FE");
    }

    @Test
    void callbackAprobadoConfirmaElPedidoYRegistraEnHistorial() {
        Pedido pedido = pedidoPendiente();
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        com.mycompany.knstore.domain.User user = new com.mycompany.knstore.domain.User();
        user.setEmail("cliente@test.com");
        user.setLogin("cliente");
        cuenta.setUser(user);
        pedido.setCuenta(cuenta);
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        when(pagoRepository.findByReferenciaPasarela("SIM-123")).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(facturaRepository.findByPagoId(any(), any(Pageable.class))).thenReturn(Page.empty());
        when(facturaPdfService.generarConsecutivo("FE")).thenReturn("FE-000001");
        when(facturaPdfService.generarPdf(any(), any())).thenReturn(new byte[] { 1, 2, 3 });

        PagoDTO result = service.procesarCallback("SIM-123", "APPROVED", new BigDecimal("120000.00"), "AUT-X1");

        assertThat(result.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMED);
        verify(historialEstadoService).registrar(eq("PEDIDO"), eq("p-1"), eq("estado"), eq("PENDING"), eq("CONFIRMED"));
        verify(historialEstadoService).registrar(eq("PAGO"), eq("pg-1"), eq("estado"), eq("PENDING"), eq("APPROVED"));
    }

    @Test
    void callbackDuplicadoNoReprocesaElPago() {
        Pedido pedido = pedidoPendiente();
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        pago.setEstado(EstadoPago.APPROVED);
        when(pagoRepository.findByReferenciaPasarela("SIM-123")).thenReturn(Optional.of(pago));

        PagoDTO result = service.procesarCallback("SIM-123", "APPROVED", new BigDecimal("120000.00"), null);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.APPROVED);
        verify(pagoRepository, never()).save(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void callbackConMontoIncoherenteRechazaElPago() {
        Pedido pedido = pedidoPendiente();
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        when(pagoRepository.findByReferenciaPasarela("SIM-123")).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PagoDTO result = service.procesarCallback("SIM-123", "APPROVED", new BigDecimal("1000.00"), null);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.REJECTED);
        assertThat(result.getDescripcionRespuesta()).contains("monto no coincide");
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void callbackDeReferenciaDesconocidaLanzaExcepcion() {
        when(pagoRepository.findByReferenciaPasarela("DESCONOCIDA")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.procesarCallback("DESCONOCIDA", "APPROVED", new BigDecimal("1000.00"), null)).isInstanceOf(
            IllegalArgumentException.class
        );
    }

    @Test
    void reembolsarPagoAprobadoDejaTrazabilidad() {
        Pedido pedido = pedidoPendiente();
        pedido.setEstado(EstadoPedido.SHIPPED);
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        pago.setEstado(EstadoPago.APPROVED);
        when(pagoRepository.findById("pg-1")).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PagoDTO result = service.reembolsar("pg-1", "Devolucion por talla incorrecta");

        assertThat(result.getEstado()).isEqualTo(EstadoPago.REFUNDED);
        assertThat(result.getMotivoReembolso()).isEqualTo("Devolucion por talla incorrecta");
        assertThat(result.getFechaReembolso()).isNotNull();
        verify(historialEstadoService).registrar(eq("PAGO"), eq("pg-1"), eq("estado"), eq("APPROVED"), eq("REFUNDED"));
        verify(historialEstadoService).registrar(eq("PEDIDO"), eq("p-1"), eq("reembolso"), any(), any());
    }

    @Test
    void reembolsarPagoNoAprobadoLanzaExcepcion() {
        Pedido pedido = pedidoPendiente();
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        pago.setEstado(EstadoPago.PENDING);
        when(pagoRepository.findById("pg-1")).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> service.reembolsar("pg-1", "prueba"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("aprobados");
    }

    @Test
    void segundoReembolsoEsRechazado() {
        Pedido pedido = pedidoPendiente();
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        pago.setEstado(EstadoPago.REFUNDED);
        when(pagoRepository.findById("pg-1")).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> service.reembolsar("pg-1", "otro motivo"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("ya fue reembolsado");
    }
}
