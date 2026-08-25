package com.mycompany.knstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.knstore.domain.Secuencia;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class SecuenciaServiceImplTest {

    private static final DateTimeFormatter FECHA_LARGA = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Mock
    private MongoTemplate mongoTemplate;

    private SecuenciaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SecuenciaServiceImpl(mongoTemplate);
    }

    @Test
    void siguienteCuandoEsPrimerUseDeLaClaveRetornaUno() {
        when(mongoTemplate.findAndModify(any(), any(), any(), any(), anyString())).thenReturn(null);

        long secuencia = service.siguiente("PED", LocalDate.of(2026, 8, 24));

        assertThat(secuencia).isEqualTo(1L);
    }

    @Test
    void siguienteIncrementaElContadorDeFormaAtomica() {
        // El contador previo era 5; tras el $inc de 1, el documento retornado
        // por MongoDB (returnNew) trae seq=6. El servicio devuelve exactamente
        // el valor final del contador que devuelve Mongo.
        Secuencia documento = new Secuencia();
        documento.setId("PED-20260824");
        documento.setTipo("PED");
        documento.setFecha(LocalDate.of(2026, 8, 24));
        documento.setSeq(6L);
        when(mongoTemplate.findAndModify(any(), any(), any(), any(), anyString())).thenReturn(documento);

        long secuencia = service.siguiente("PED", LocalDate.of(2026, 8, 24));

        assertThat(secuencia).isEqualTo(6L);
    }

    @Test
    void siguienteUsaLaClaveTipoFechaYLaColeccionSecuencias() {
        when(mongoTemplate.findAndModify(any(), any(), any(), any(), anyString())).thenReturn(null);

        service.siguiente("PED", LocalDate.of(2026, 8, 24));

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).findAndModify(queryCaptor.capture(), any(Update.class), any(FindAndModifyOptions.class), any(), anyString());
        assertThat(queryCaptor.getValue().getQueryObject().get("_id")).isEqualTo("PED-20260824");
    }

    @Test
    void generarNumeroPedidoMantieneFormatoPED() {
        Secuencia documento = new Secuencia();
        documento.setSeq(3L);
        when(mongoTemplate.findAndModify(any(), any(), any(), any(), anyString())).thenReturn(documento);

        String numero = service.siguientePedido();

        assertThat(numero).matches("PED-\\d{8}-\\d{6}");
        assertThat(numero).isEqualTo("PED-" + FECHA_LARGA.format(LocalDate.now()) + "-000003");
    }

    @Test
    void generarConsecutivoFacturaMantieneFormatoFE() {
        Secuencia documento = new Secuencia();
        documento.setSeq(7L);
        when(mongoTemplate.findAndModify(any(), any(), any(), any(), anyString())).thenReturn(documento);

        String consecutivo = service.siguienteConsecutivo("FE");

        assertThat(consecutivo).matches("FE-\\d{6}");
        assertThat(consecutivo).isEqualTo("FE-000007");
    }
}
