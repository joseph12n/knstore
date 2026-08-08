package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.mapper.PedidoMapper;
import com.mycompany.knstore.service.mapper.PedidoMapperImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private ProductoInventarioRepository productoInventarioRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private HistorialEstadoService historialEstadoService;

    private PedidoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PedidoServiceImpl(
            pedidoRepository,
            cuentaRepository,
            itemPedidoRepository,
            productoInventarioRepository,
            mongoTemplate,
            pedidoMapper,
            historialEstadoService
        );
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
}
