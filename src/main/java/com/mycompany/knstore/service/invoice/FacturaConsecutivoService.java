package com.mycompany.knstore.service.invoice;

import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class FacturaConsecutivoService {

    private static final String FACTURA_SEQUENCE_COLLECTION = "factura_sequence";
    private static final String FACTURA_SEQUENCE_KEY = "FAC";

    private final MongoTemplate mongoTemplate;

    public FacturaConsecutivoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String siguienteNumeroFactura() {
        Query query = new Query(Criteria.where("_id").is(FACTURA_SEQUENCE_KEY));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
        Document sequence = mongoTemplate.findAndModify(query, update, options, Document.class, FACTURA_SEQUENCE_COLLECTION);
        long seq = sequence != null ? ((Number) sequence.get("seq")).longValue() : 1L;
        return String.format("FAC-%06d", seq);
    }
}
