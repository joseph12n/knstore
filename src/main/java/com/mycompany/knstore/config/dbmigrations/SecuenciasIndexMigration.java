package com.mycompany.knstore.config.dbmigrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

/**
 * Índice único (tipo + fecha) de la colección {@code secuencias} (RNF-030):
 * garantiza un único contador diario por clase de consecutivo, evitando que dos
 * documentos compitan al mismo tiempo por el mismo día.
 */
@ChangeUnit(id = "secuencias-daily-index", order = "008")
public class SecuenciasIndexMigration {

    private final MongoTemplate template;

    public SecuenciasIndexMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        template
            .indexOps("secuencias")
            .ensureIndex(
                new Index("tipo", Sort.Direction.ASC).on("fecha", Sort.Direction.ASC).named("unique_secuencia_tipo_fecha").unique()
            );
    }

    @RollbackExecution
    public void rollback() {
        template.indexOps("secuencias").dropIndex("unique_secuencia_tipo_fecha");
    }
}
