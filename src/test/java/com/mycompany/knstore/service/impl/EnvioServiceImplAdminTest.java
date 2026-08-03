package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplAdminTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private EnvioMapper envioMapper;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @InjectMocks
    private EnvioServiceImpl envioService;

    @Test
    void findPendientesAdminFiltraEstadosOperativos() {
        Envio envio = new Envio();
        envio.setId("env-1");
        envio.setEstado(EstadoEnvio.PENDING);

        when(envioRepository.findByEstadoIn(any(), any())).thenReturn(new PageImpl<>(List.of(envio), PageRequest.of(0, 10), 1));
        when(envioMapper.toDto(envio)).thenReturn(new EnvioDTO());

        assertThat(envioService.findPendientesAdmin(PageRequest.of(0, 10)).getTotalElements()).isEqualTo(1);
    }

    @Test
    void asignarNumeroRastreoAsignaTrackingYDespacho() {
        Envio envio = new Envio();
        envio.setId("env-1");
        envio.setEstado(EstadoEnvio.PENDING);

        when(envioRepository.findByNumeroRastreo("TRK-100")).thenReturn(Optional.empty());
        when(envioRepository.findById("env-1")).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(envioMapper.toDto(any(Envio.class))).thenAnswer(invocation -> {
            Envio saved = invocation.getArgument(0);
            EnvioDTO dto = new EnvioDTO();
            dto.setId(saved.getId());
            dto.setEstado(saved.getEstado());
            dto.setNumeroRastreo(saved.getNumeroRastreo());
            return dto;
        });

        EnvioDTO result = envioService.asignarNumeroRastreo("env-1", "TRK-100", "DHL", "https://track").orElseThrow();

        assertThat(result.getNumeroRastreo()).isEqualTo("TRK-100");
        assertThat(result.getEstado()).isEqualTo(EstadoEnvio.DISPATCHED);
        assertThat(envio.getFechaDespacho()).isBeforeOrEqualTo(Instant.now());
        verify(historialEstadoService).registrarCambioEstado("Envio", "env-1", "numeroRastreo", null, "TRK-100");
    }

    @Test
    void asignarNumeroRastreoRechazaDuplicado() {
        Envio otro = new Envio();
        otro.setId("env-2");
        when(envioRepository.findByNumeroRastreo("TRK-100")).thenReturn(Optional.of(otro));

        assertThrows(IllegalArgumentException.class, () -> envioService.asignarNumeroRastreo("env-1", "TRK-100", "DHL", "https://track"));
    }
}
