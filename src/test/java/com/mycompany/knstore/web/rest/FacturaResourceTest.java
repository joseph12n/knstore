package com.mycompany.knstore.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.User;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.service.FacturaService;
import com.mycompany.knstore.service.MailService;
import com.mycompany.knstore.service.invoice.FacturaPdfService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FacturaResourceTest {

    @Mock
    private FacturaService facturaService;

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private FacturaPdfService facturaPdfService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private FacturaResource facturaResource;

    @Test
    void getFacturaPdfRetornaPdfConHeaders() {
        Factura factura = new Factura();
        factura.setId("f-1");
        factura.setNumeroFactura("FAC-000010");
        factura.setTotal(new BigDecimal("50.00"));
        factura.setEnviada(Boolean.FALSE);

        when(facturaRepository.findById("f-1")).thenReturn(Optional.of(factura));
        when(facturaPdfService.generarPdf(factura)).thenReturn("PDF".getBytes());

        ResponseEntity<byte[]> response = facturaResource.getFacturaPdf("f-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(response.getHeaders().getFirst("Content-Disposition")).contains("FAC-000010");
        assertThat(response.getBody()).isEqualTo("PDF".getBytes());
    }

    @Test
    void enviarFacturaPorEmailMarcaFacturaYEnviaAdjunto() {
        Factura factura = new Factura();
        factura.setId("f-2");
        factura.setNumeroFactura("FAC-000011");
        factura.setTotal(new BigDecimal("80.00"));
        factura.setEnviada(Boolean.FALSE);

        User user = new User();
        user.setEmail("cliente@example.com");
        Cuenta cuenta = new Cuenta();
        cuenta.setUser(user);
        Pedido pedido = new Pedido();
        pedido.setCuenta(cuenta);
        Pago pago = new Pago();
        pago.setPedido(pedido);
        factura.setPago(pago);

        when(facturaRepository.findById("f-2")).thenReturn(Optional.of(factura));
        when(facturaPdfService.generarPdf(factura)).thenReturn(new byte[] { 1, 2, 3 });
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Void> response = facturaResource.enviarFacturaPorEmail("f-2");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(factura.getEnviada()).isTrue();
        assertThat(factura.getFechaEnvioEmail()).isBeforeOrEqualTo(Instant.now());
        verify(mailService).sendEmailWithAttachment(
            "cliente@example.com",
            "Factura FAC-000011",
            "Adjuntamos su factura FAC-000011. Gracias por su compra en KN Store.",
            "factura-FAC-000011.pdf",
            new byte[] { 1, 2, 3 }
        );
        verify(facturaRepository).save(factura);
    }
}
