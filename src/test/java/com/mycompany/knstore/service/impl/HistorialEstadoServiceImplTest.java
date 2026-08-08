package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.HistorialEstado;
import com.mycompany.knstore.repository.HistorialEstadoRepository;
import com.mycompany.knstore.service.dto.HistorialEstadoDTO;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HistorialEstadoServiceImplTest {

    @Mock
    private HistorialEstadoRepository historialEstadoRepository;

    private HistorialEstadoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new HistorialEstadoServiceImpl(historialEstadoRepository);
    }

    @Test
    void registrarPersisteTransicionConFechaYActor() {
        when(historialEstadoRepository.save(any())).thenAnswer(invocation -> {
            HistorialEstado historial = invocation.getArgument(0);
            historial.setId("h-1");
            return historial;
        });

        service.registrar("PEDIDO", "pedido-1", "estado", "PENDING", "CONFIRMED");

        ArgumentCaptor<HistorialEstado> captor = ArgumentCaptor.forClass(HistorialEstado.class);
        verify(historialEstadoRepository).save(captor.capture());
        HistorialEstado guardado = captor.getValue();
        assertThat(guardado.getEntidad()).isEqualTo("PEDIDO");
        assertThat(guardado.getIdEntidad()).isEqualTo("pedido-1");
        assertThat(guardado.getCampo()).isEqualTo("estado");
        assertThat(guardado.getValorAnterior()).isEqualTo("PENDING");
        assertThat(guardado.getValorNuevo()).isEqualTo("CONFIRMED");
        assertThat(guardado.getFecha()).isNotNull();
        assertThat(guardado.getActor()).isNotEmpty();
    }

    @Test
    void consultarDevuelveTransicionesOrdenadasPorFecha() {
        HistorialEstado primero = historial("h-1", "PENDING", "CONFIRMED", Instant.now().minusSeconds(60));
        HistorialEstado segundo = historial("h-2", "CONFIRMED", "SHIPPED", Instant.now());
        when(historialEstadoRepository.findByEntidadAndIdEntidadOrderByFechaAsc("PEDIDO", "pedido-1")).thenReturn(
            List.of(primero, segundo)
        );

        List<HistorialEstadoDTO> result = service.consultar("PEDIDO", "pedido-1");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getValorNuevo()).isEqualTo("CONFIRMED");
        assertThat(result.get(1).getValorNuevo()).isEqualTo("SHIPPED");
        assertThat(result.get(1).getActor()).isEqualTo("admin");
    }

    private HistorialEstado historial(String id, String anterior, String nuevo, Instant fecha) {
        HistorialEstado historial = new HistorialEstado();
        historial.setId(id);
        historial.setEntidad("PEDIDO");
        historial.setIdEntidad("pedido-1");
        historial.setCampo("estado");
        historial.setValorAnterior(anterior);
        historial.setValorNuevo(nuevo);
        historial.setFecha(fecha);
        historial.setActor("admin");
        return historial;
    }
}
