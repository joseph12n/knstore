package com.mycompany.knstore.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

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
    private PedidoMapper pedidoMapper;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Test
    void saveRegistraTransicionInicialDeEstado() {
        PedidoDTO dto = new PedidoDTO();
        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        pedido.setNumeroPedido("PED-20260802-000001");
        pedido.setEstado(EstadoPedido.CONFIRMED);

        when(pedidoMapper.toEntity(dto)).thenReturn(pedido);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toDto(any(Pedido.class))).thenReturn(new PedidoDTO());

        pedidoService.save(dto);

        verify(historialEstadoService).registrarCambioEstado("Pedido", "pedido-1", "estado", null, EstadoPedido.CONFIRMED.name());
    }

    @Test
    void updateNoRegistraHistorialSiEstadoNoCambia() {
        PedidoDTO dto = new PedidoDTO();
        Pedido pedido = new Pedido();
        pedido.setId("pedido-1");
        pedido.setEstado(EstadoPedido.PENDING);

        Pedido existente = new Pedido();
        existente.setId("pedido-1");
        existente.setEstado(EstadoPedido.PENDING);

        when(pedidoMapper.toEntity(dto)).thenReturn(pedido);
        when(pedidoRepository.findById("pedido-1")).thenReturn(Optional.of(existente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toDto(any(Pedido.class))).thenReturn(new PedidoDTO());

        pedidoService.update(dto);

        verify(historialEstadoService, never()).registrarCambioEstado(any(), any(), any(), any(), any());
    }

    @Test
    void partialUpdateRegistraHistorialCuandoEstadoCambia() {
        PedidoDTO patch = new PedidoDTO();
        patch.setId("pedido-1");
        patch.setEstado(EstadoPedido.CANCELLED);

        Pedido existente = new Pedido();
        existente.setId("pedido-1");
        existente.setEstado(EstadoPedido.PENDING);

        when(pedidoRepository.findById("pedido-1")).thenReturn(Optional.of(existente));
        doAnswer(invocation -> {
            Pedido target = invocation.getArgument(0);
            PedidoDTO source = invocation.getArgument(1);
            target.setEstado(source.getEstado());
            return null;
        })
            .when(pedidoMapper)
            .partialUpdate(any(Pedido.class), any(PedidoDTO.class));
        when(itemPedidoRepository.findByPedidoId("pedido-1")).thenReturn(List.of());
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toDto(any(Pedido.class))).thenReturn(new PedidoDTO());

        pedidoService.partialUpdate(patch);

        verify(historialEstadoService).registrarCambioEstado(
            "Pedido",
            "pedido-1",
            "estado",
            EstadoPedido.PENDING.name(),
            EstadoPedido.CANCELLED.name()
        );
    }
}
