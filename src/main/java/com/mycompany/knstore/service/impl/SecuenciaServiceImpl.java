package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Secuencia;
import com.mycompany.knstore.service.SecuenciaService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Implementación de {@link SecuenciaService} sobre la colección
 * {@code secuencias} (RNF-030). Cada consecutivo diario se incrementa con
 * $inc atómico (upsert + returnNew), por lo que pedidos y facturas nunca pueden
 * repetir números incluso bajo concurrencia.
 */
@Service
public class SecuenciaServiceImpl implements SecuenciaService {

    private static final DateTimeFormatter CLAVE_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** Nombre de la colección única de contadores (RNF-030). */
    static final String COLLECTION_SECUENCIAS = "secuencias";

    private final MongoTemplate mongoTemplate;

    public SecuenciaServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public long siguiente(String tipo, LocalDate fecha) {
        String clave = tipo + "-" + fecha.format(CLAVE_FECHA);
        Query query = new Query(Criteria.where("_id").is(clave));
        // el upsert solo escribe campos del Update: sin "tipo"/"fecha" el documento
        // quedaria nulo y el indice unico (tipo, fecha) duplicaria claves (E11000)
        Update update = new Update().set("tipo", tipo).set("fecha", fecha).inc("seq", 1L);
        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
        Secuencia secuencia = mongoTemplate.findAndModify(query, update, options, Secuencia.class, COLLECTION_SECUENCIAS);
        return secuencia != null ? secuencia.getSeq() : 1L;
    }

    @Override
    public String siguientePedido() {
        String fecha = LocalDate.now().format(CLAVE_FECHA);
        return "PED-%s-%06d".formatted(fecha, siguiente("PED", LocalDate.now()));
    }

    @Override
    public String siguienteConsecutivo(String prefijo) {
        String prefijoBase = prefijo == null || prefijo.isBlank() ? "FE" : prefijo;
        return "%s-%06d".formatted(prefijoBase, siguiente(prefijoBase, LocalDate.now()));
    }
}
