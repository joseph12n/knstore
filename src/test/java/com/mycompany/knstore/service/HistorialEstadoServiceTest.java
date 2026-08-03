package com.mycompany.knstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.mycompany.knstore.domain.HistorialEstado;
import com.mycompany.knstore.repository.HistorialEstadoRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class HistorialEstadoServiceTest {

    @Mock
    private HistorialEstadoRepository historialEstadoRepository;

    private HistorialEstadoService historialEstadoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        historialEstadoService = new HistorialEstadoService(historialEstadoRepository);
    }

    @Test
    void registrarCambioEstadoPersisteEventoConFechaYActor() {
        historialEstadoService.registrarCambioEstado("Pedido", "pedido-1", "estado", "PENDING", "CONFIRMED");

        ArgumentCaptor<HistorialEstado> captor = ArgumentCaptor.forClass(HistorialEstado.class);
        verify(historialEstadoRepository).save(captor.capture());

        HistorialEstado historial = captor.getValue();
        assertThat(historial.getEntidad()).isEqualTo("Pedido");
        assertThat(historial.getIdEntidad()).isEqualTo("pedido-1");
        assertThat(historial.getCampo()).isEqualTo("estado");
        assertThat(historial.getValorAnterior()).isEqualTo("PENDING");
        assertThat(historial.getValorNuevo()).isEqualTo("CONFIRMED");
        assertThat(historial.getFecha()).isNotNull();
        assertThat(historial.getActor()).isNotBlank();
    }

    @Test
    void obtenerHistorialEntidadDelegatesToRepository() {
        historialEstadoService.obtenerHistorialEntidad("Pago", "pago-1");

        verify(historialEstadoRepository).findByEntidadAndIdEntidadOrderByFechaDesc("Pago", "pago-1");
    }
}
