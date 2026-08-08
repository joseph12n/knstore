package com.mycompany.knstore.service.invoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pedido;
import java.math.BigDecimal;
import java.time.Instant;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class FacturaPdfServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private Factura factura(String numero) {
        Factura factura = new Factura();
        factura.setId("f-1");
        factura.setNumero(numero);
        factura.setSubtotal(new BigDecimal("100000.00"));
        factura.setValorIva(new BigDecimal("19000.00"));
        factura.setDescuentos(BigDecimal.ZERO);
        factura.setTotal(new BigDecimal("119000.00"));
        factura.setFechaEmision(Instant.parse("2026-08-01T10:00:00Z"));
        return factura;
    }

    @Test
    void consecutivoSeGeneraConFormatoEsperado() {
        Document sequence = new Document("seq", 7L);
        when(mongoTemplate.findAndModify(any(), any(), any(), any(), any(String.class))).thenReturn(sequence);

        FacturaPdfService service = new FacturaPdfService(mongoTemplate);
        String consecutivo = service.generarConsecutivo("FE");

        assertThat(consecutivo).isEqualTo("FE-000007");
    }

    @Test
    void payloadDeValidacionContieneNumeroFechaTotalYHash() {
        FacturaPdfService service = new FacturaPdfService(mongoTemplate);
        Factura factura = factura("FE-000001");

        String payload = service.generarPayloadValidacion(factura);

        assertThat(payload).startsWith("FE-000001|2026-08-01T10:00:00Z|119000.00|");
        String[] partes = payload.split("\\|");
        assertThat(partes).hasSize(4);
        assertThat(partes[3]).hasSize(64);
    }

    @Test
    void generarPdfProduceBytesValidos() {
        FacturaPdfService service = new FacturaPdfService(mongoTemplate);
        Pedido pedido = new Pedido();
        pedido.setId("p-1");

        byte[] pdf = service.generarPdf(factura("FE-000001"), pedido);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 5, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }
}
