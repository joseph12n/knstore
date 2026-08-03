package com.mycompany.knstore.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.mapper.PagoMapper;
import com.mycompany.knstore.service.payment.PaymentGateway;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

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
    void saveRegistraTransicionInicialDeEstado() {
        PagoDTO dto = new PagoDTO();
        Pago pago = new Pago();
        pago.setId("pago-1");
        pago.setEstado(EstadoPago.PENDING);

        when(pagoMapper.toEntity(dto)).thenReturn(pago);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toDto(any(Pago.class))).thenReturn(new PagoDTO());

        pagoService.save(dto);

        verify(historialEstadoService).registrarCambioEstado("Pago", "pago-1", "estado", null, EstadoPago.PENDING.name());
    }

    @Test
    void updateNoRegistraHistorialSiEstadoNoCambia() {
        PagoDTO dto = new PagoDTO();
        Pago pago = new Pago();
        pago.setId("pago-1");
        pago.setEstado(EstadoPago.APPROVED);

        Pago existente = new Pago();
        existente.setId("pago-1");
        existente.setEstado(EstadoPago.APPROVED);

        when(pagoMapper.toEntity(dto)).thenReturn(pago);
        when(pagoRepository.findById("pago-1")).thenReturn(Optional.of(existente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toDto(any(Pago.class))).thenReturn(new PagoDTO());

        pagoService.update(dto);

        verify(historialEstadoService, never()).registrarCambioEstado(any(), any(), any(), any(), any());
    }

    @Test
    void partialUpdateRegistraHistorialCuandoEstadoCambia() {
        PagoDTO patch = new PagoDTO();
        patch.setId("pago-1");
        patch.setEstado(EstadoPago.REJECTED);

        Pago existente = new Pago();
        existente.setId("pago-1");
        existente.setEstado(EstadoPago.PENDING);

        when(pagoRepository.findById("pago-1")).thenReturn(Optional.of(existente));
        doAnswer(invocation -> {
            Pago target = invocation.getArgument(0);
            PagoDTO source = invocation.getArgument(1);
            target.setEstado(source.getEstado());
            return null;
        })
            .when(pagoMapper)
            .partialUpdate(any(Pago.class), any(PagoDTO.class));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoMapper.toDto(any(Pago.class))).thenReturn(new PagoDTO());

        pagoService.partialUpdate(patch);

        verify(historialEstadoService).registrarCambioEstado(
            "Pago",
            "pago-1",
            "estado",
            EstadoPago.PENDING.name(),
            EstadoPago.REJECTED.name()
        );
    }
}
