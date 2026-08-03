package com.mycompany.knstore.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.CheckoutService;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.PedidoService;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.dto.PedidoEstadoUpdateRequestDTO;
import com.mycompany.knstore.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PedidoResourceS08Test {

    @Mock
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CheckoutService checkoutService;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @InjectMocks
    private PedidoResource pedidoResource;

    @Test
    void actualizarEstadoPedidoRechazaEstadoIgual() {
        PedidoDTO actual = new PedidoDTO();
        actual.setId("ped-1");
        actual.setEstado(EstadoPedido.SHIPPED);
        when(pedidoService.findOne("ped-1")).thenReturn(Optional.of(actual));

        PedidoEstadoUpdateRequestDTO request = new PedidoEstadoUpdateRequestDTO();
        request.setEstado(EstadoPedido.SHIPPED);

        assertThrows(BadRequestAlertException.class, () -> pedidoResource.actualizarEstadoPedido("ped-1", request));
    }

    @Test
    void actualizarEstadoPedidoActualizaYRegistraMotivo() {
        PedidoDTO actual = new PedidoDTO();
        actual.setId("ped-1");
        actual.setEstado(EstadoPedido.PENDING);
        when(pedidoService.findOne("ped-1")).thenReturn(Optional.of(actual));

        PedidoDTO actualizado = new PedidoDTO();
        actualizado.setId("ped-1");
        actualizado.setEstado(EstadoPedido.CONFIRMED);
        when(pedidoService.partialUpdate(any(PedidoDTO.class))).thenReturn(Optional.of(actualizado));

        PedidoEstadoUpdateRequestDTO request = new PedidoEstadoUpdateRequestDTO();
        request.setEstado(EstadoPedido.CONFIRMED);
        request.setMotivo("Validacion manual");

        ResponseEntity<PedidoDTO> response = pedidoResource.actualizarEstadoPedido("ped-1", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo(EstadoPedido.CONFIRMED);
        verify(historialEstadoService).registrarCambioEstado("Pedido", "ped-1", "motivoEstado", null, "Validacion manual");
    }
}
