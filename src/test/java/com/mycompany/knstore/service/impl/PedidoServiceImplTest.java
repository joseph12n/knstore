package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.PagoService;
import com.mycompany.knstore.service.SecuenciaService;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.mapper.PedidoMapper;
import com.mycompany.knstore.service.mapper.PedidoMapperImpl;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    private final PedidoMapper pedidoMapper = new PedidoMapperImpl();

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @Mock
    private SecuenciaService secuenciaService;

    @Mock
    private PagoService pagoService;

    @Mock
    private PagoRepository pagoRepository;

    private PedidoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PedidoServiceImpl(
            pedidoRepository,
            cuentaRepository,
            itemPedidoRepository,
            mongoTemplate,
            pedidoMapper,
            historialEstadoService,
            secuenciaService,
            pagoService,
            pagoRepository
        );
    }

    @Test
    void saveAsignaNumeroPedidoConFormatoPEDCuandoVieneVacio() {
        PedidoDTO dto = new PedidoDTO();
        dto.setEstado(EstadoPedido.PENDING);
        when(secuenciaService.siguientePedido()).thenReturn("PED-20260824-000003");
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoDTO result = service.save(dto);

        assertThat(result.getNumeroPedido()).isEqualTo("PED-20260824-000003");
        verify(secuenciaService).siguientePedido();
    }

    @Test
    void cambiarEstadoValidoActualizaYRegistraEnHistorial() {
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setEstado(EstadoPedido.CONFIRMED);
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoDTO result = service.cambiarEstado("p-1", EstadoPedido.SHIPPED);

        assertThat(result.getEstado()).isEqualTo(EstadoPedido.SHIPPED);
        verify(historialEstadoService).registrar(eq("PEDIDO"), eq("p-1"), eq("estado"), eq("CONFIRMED"), eq("SHIPPED"));
    }

    @Test
    void cambiarEstadoInvalidoLanzaExcepcion() {
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setEstado(EstadoPedido.DELIVERED);
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> service.cambiarEstado("p-1", EstadoPedido.CANCELLED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Transicion invalida");
    }

    @Test
    void cancelarSoloDesdePendienteOConfirmado() {
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setEstado(EstadoPedido.PENDING);
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoDTO result = service.cambiarEstado("p-1", EstadoPedido.CANCELLED);

        assertThat(result.getEstado()).isEqualTo(EstadoPedido.CANCELLED);
    }

    @Test
    void cancelarPedidoClienteDentroDeLaHoraReembolsaElPagoAprobadoYCancela() {
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setCreatedDate(Instant.now());
        pedido.setEstado(EstadoPedido.CONFIRMED);
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Pago pagoAprobado = new Pago();
        pagoAprobado.setId("pago-1");
        pagoAprobado.setEstado(EstadoPago.APPROVED);
        when(pagoRepository.findByPedidoId(eq("p-1"), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(pagoAprobado)));
        PagoDTO pagoReembolsado = new PagoDTO();
        pagoReembolsado.setId("pago-1");
        pagoReembolsado.setEstado(EstadoPago.REFUNDED);
        when(pagoService.reembolsar(eq("pago-1"), any(String.class))).thenReturn(pagoReembolsado);

        PedidoDTO result = service.cancelarPedidoCliente("p-1", "Ya no lo necesito");

        assertThat(result.getEstado()).isEqualTo(EstadoPedido.CANCELLED);
        verify(pagoService).reembolsar(eq("pago-1"), eq("Ya no lo necesito"));
        verify(historialEstadoService).registrar(eq("PEDIDO"), eq("p-1"), eq("estado"), eq("CONFIRMED"), eq("CANCELLED"));
    }

    @Test
    void cancelarPedidoClienteDentroDeLaHoraSinPagoAprobadoSoloCancela() {
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setCreatedDate(Instant.now());
        pedido.setEstado(EstadoPedido.PENDING);
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoRepository.findByPedidoId(eq("p-1"), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

        PedidoDTO result = service.cancelarPedidoCliente("p-1", null);

        assertThat(result.getEstado()).isEqualTo(EstadoPedido.CANCELLED);
        verify(pagoService, org.mockito.Mockito.never()).reembolsar(any(String.class), any(String.class));
    }

    @Test
    void cancelarPedidoClienteFueraDeLaVentanaDeUnaHoraLanzaExcepcionYNoReembolsa() {
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setCreatedDate(Instant.now().minusSeconds(2 * 60 * 60));
        pedido.setEstado(EstadoPedido.CONFIRMED);
        when(pedidoRepository.findById("p-1")).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> service.cancelarPedidoCliente("p-1", null))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("1 hora");
        verify(pagoService, org.mockito.Mockito.never()).reembolsar(any(String.class), any(String.class));
        verify(pedidoRepository, org.mockito.Mockito.never()).save(any());
    }
}
