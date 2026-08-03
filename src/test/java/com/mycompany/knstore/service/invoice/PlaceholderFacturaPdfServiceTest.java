package com.mycompany.knstore.service.invoice;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.knstore.domain.Factura;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PlaceholderFacturaPdfServiceTest {

    private final PlaceholderFacturaPdfService service = new PlaceholderFacturaPdfService();

    @Test
    void generarPdfDebeRetornarDocumentoValido() {
        Factura factura = new Factura();
        factura.setId("factura-1");
        factura.setPrefijo("FAC");
        factura.setNumeroFactura("FAC-000001");
        factura.setCufe("CUFE-TEST");
        factura.setSubtotal(new BigDecimal("100.00"));
        factura.setDescuentos(new BigDecimal("5.00"));
        factura.setBaseGravableIva(new BigDecimal("95.00"));
        factura.setValorIva(new BigDecimal("18.05"));
        factura.setTotal(new BigDecimal("113.05"));
        factura.setEnviada(Boolean.FALSE);
        factura.setFechaEmision(Instant.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(15));
        factura.setNotasAdicionales("Factura de prueba");

        byte[] pdf = service.generarPdf(factura);

        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(500);
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }
}
