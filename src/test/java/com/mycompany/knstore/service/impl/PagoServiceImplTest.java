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
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.MailService;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.invoice.FacturaPdfService;
import com.mycompany.knstore.service.mapper.PagoMapper;
import com.mycompany.knstore.service.mapper.PagoMapperImpl;
import com.mycompany.knstore.service.payment.PaymentGateway;
import com.mycompany.knstore.service.payment.SimulatedPaymentGateway;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
        paymentGateway = new SimulatedPaymentGateway();
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

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(String login, String... authorities) {
        Jwt jwt = Jwt.withTokenValue("dummy-token")
            .header("alg", "HS512")
            .subject(login)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .claim(SecurityUtils.AUTHORITIES_CLAIM, String.join(" ", authorities))
            .claim(SecurityUtils.USER_ID_CLAIM, login)
            .build();
        List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
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
    void callbackConMontoIncoherenteRechazaElPagoSoloPorMonto() {
        Pedido pedido = pedidoPendiente();
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        when(pagoRepository.findByReferenciaPasarela("SIM-123")).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // La pasarela simbolica aprueba siempre, pero el monto no coincide con el pedido.
        PagoDTO result = service.procesarCallback("SIM-123", "APPROVED", new BigDecimal("1000.00"), null);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.REJECTED);
        assertThat(result.getDescripcionRespuesta()).contains("monto no coincide");
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void callbackConEstadoRechazadoNoBajaUnPagoYaAprobado() {
        Pedido pedido = pedidoPendiente();
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        pago.setEstado(EstadoPago.APPROVED);
        pago.setCodigoAutorizacion("AUT-X1");
        when(pagoRepository.findByReferenciaPasarela("SIM-123")).thenReturn(Optional.of(pago));

        PagoDTO result = service.procesarCallback("SIM-123", "REJECTED", new BigDecimal("120000.00"), null);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(result.getCodigoAutorizacion()).isEqualTo("AUT-X1");
        verify(pagoRepository, never()).save(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void dobleIniciarPagoNoReprocesaUnPagoYaAprobado() {
        Pedido pedido = pedidoPendiente();
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        pago.setEstado(EstadoPago.APPROVED);
        pago.setCodigoAutorizacion("AUT-X1");
        pago.setFechaPago(java.time.Instant.now());
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));
        when(pagoRepository.findByPedidoId(eq("p-1"), any(Pageable.class))).thenReturn(new PageImpl<>(java.util.List.of(pago)));

        PagoDTO result = service.iniciarPago("p-1");

        assertThat(result.getEstado()).isEqualTo(EstadoPago.APPROVED);
        assertThat(result.getReferenciaPasarela()).isEqualTo("SIM-123");
        assertThat(result.getCodigoAutorizacion()).isEqualTo("AUT-X1");
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void callbackDePedidoCanceladoRechazaElPagoSinConfirmar() {
        Pedido pedido = pedidoPendiente();
        pedido.setEstado(EstadoPedido.CANCELLED);
        Pago pago = pagoPendiente("pg-1", "SIM-123", new BigDecimal("120000.00"), pedido);
        when(pagoRepository.findByReferenciaPasarela("SIM-123")).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // La pasarela simbolica aprueba siempre, pero la maquina de estados del pedido lo impide.
        PagoDTO result = service.procesarCallback("SIM-123", "APPROVED", new BigDecimal("120000.00"), null);

        assertThat(result.getEstado()).isEqualTo(EstadoPago.REJECTED);
        assertThat(result.getDescripcionRespuesta()).contains("cancelado");
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CANCELLED);
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

    @Test
    void findAllClienteUsaConsultaEnLotePorPedidosDeLaCuenta() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByCuentaId("cuenta-1", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(pedido)));

        Pago pago = new Pago();
        pago.setId("pg-1");
        pago.setPedido(pedido);
        when(pagoRepository.findByPedidoIdIn(any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(pago)));

        Page<PagoDTO> result = service.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
            .singleElement()
            .satisfies(dto -> assertThat(dto.getId()).isEqualTo("pg-1"));
        verify(pedidoRepository).findByCuentaId(eq("cuenta-1"), eq(Pageable.unpaged()));
        verify(pagoRepository).findByPedidoIdIn(any(), any(Pageable.class));
        verify(pagoRepository, never()).findByPedidoId(any(), any(Pageable.class));
    }

    @Test
    void findOneClienteVerificaOwnershipSinRecorrerListas() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByIdAndCuentaId("p-1", "cuenta-1")).thenReturn(Optional.of(pedido));

        Pago pago = new Pago();
        pago.setId("pg-1");
        pago.setPedido(pedido);
        when(pagoRepository.findById("pg-1")).thenReturn(Optional.of(pago));

        Optional<PagoDTO> result = service.findOne("pg-1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo("pg-1");
        verify(pagoRepository).findById("pg-1");
        verify(pedidoRepository).findByIdAndCuentaId(eq("p-1"), eq("cuenta-1"));
        verify(pedidoRepository, never()).findByCuentaId(any(), any());
        verify(pagoRepository, never()).findByPedidoId(any(), any(Pageable.class));
    }

    @Test
    void findOneClienteConPedidoDeOtraCuentaDevuelveVacio() {
        authenticate("cliente", AuthoritiesConstants.CLIENTE);
        com.mycompany.knstore.domain.Cuenta cuenta = new com.mycompany.knstore.domain.Cuenta();
        cuenta.setId("cuenta-1");
        when(cuentaRepository.findOneByUserId("cliente")).thenReturn(Optional.of(cuenta));

        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        when(pedidoRepository.findByIdAndCuentaId("p-1", "cuenta-1")).thenReturn(Optional.empty());

        Pago pago = new Pago();
        pago.setId("pg-1");
        pago.setPedido(pedido);
        when(pagoRepository.findById("pg-1")).thenReturn(Optional.of(pago));

        Optional<PagoDTO> result = service.findOne("pg-1");

        assertThat(result).isEmpty();
    }
}
