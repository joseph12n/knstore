package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
import com.mycompany.knstore.service.mapper.EnvioMapperImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    private final EnvioMapper envioMapper = new EnvioMapperImpl();

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private HistorialEstadoService historialEstadoService;

    private EnvioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EnvioServiceImpl(envioRepository, pedidoRepository, cuentaRepository, envioMapper, historialEstadoService);
    }

    private Envio envioConEstado(EstadoEnvio estado) {
        Envio envio = new Envio();
        envio.setId("e-1");
        envio.setEstado(estado);
        return envio;
    }

    @Test
    void asignarTrackingGuardaTransportadoraYNumero() {
        Envio envio = envioConEstado(EstadoEnvio.PENDING);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO result = service.asignarTracking("e-1", "Interrapidismo", "TRK-123");

        assertThat(result.getTransportadora()).isEqualTo("Interrapidismo");
        assertThat(result.getNumeroRastreo()).isEqualTo("TRK-123");
        verify(historialEstadoService).registrar(eq("ENVIO"), eq("e-1"), eq("tracking"), any(), any());
    }

    @Test
    void cambiarEstadoAvanceValidoRegistraEnHistorial() {
        Envio envio = envioConEstado(EstadoEnvio.PENDING);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO result = service.cambiarEstado("e-1", EstadoEnvio.IN_TRANSIT);

        assertThat(result.getEstado()).isEqualTo(EstadoEnvio.IN_TRANSIT);
        verify(historialEstadoService).registrar(eq("ENVIO"), eq("e-1"), eq("estado"), eq("PENDING"), eq("IN_TRANSIT"));
    }

    @Test
    void cambiarEstadoInvalidoLanzaExcepcion() {
        Envio envio = envioConEstado(EstadoEnvio.DELIVERED);
        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));

        assertThatThrownBy(() -> service.cambiarEstado("e-1", EstadoEnvio.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Transicion invalida");
    }

    @Test
    void marcarDevolucionMarcaEnvioYPedidoDevueltos() {
        Envio envio = envioConEstado(EstadoEnvio.IN_TRANSIT);
        Pedido pedido = new Pedido();
        pedido.setId("p-1");
        pedido.setEstado(EstadoPedido.SHIPPED);
        envio.setPedido(pedido);

        when(envioRepository.findById("e-1")).thenReturn(Optional.of(envio));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO result = service.marcarDevolucion("e-1");

        assertThat(result.getEstado()).isEqualTo(EstadoEnvio.RETURNED);
        verify(historialEstadoService).registrar(eq("PEDIDO"), eq("p-1"), eq("estado"), eq("SHIPPED"), eq("RETURNED"));
    }
}
