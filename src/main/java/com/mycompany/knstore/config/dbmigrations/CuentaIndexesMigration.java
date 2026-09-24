package com.mycompany.knstore.config.dbmigrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.PartialIndexFilter;

/**
 * Crea el indice unico de documento del perfil (tipo de documento + numero),
 * parcial para no afectar documentos antiguos sin esos campos.
 */
@ChangeUnit(id = "cuenta-documento-index", order = "004")
public class CuentaIndexesMigration {

    private final MongoTemplate template;

    public CuentaIndexesMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        template.indexOps("cuenta").ensureIndex(
            new Index("tipoDocumento.$id", Sort.Direction.ASC)
                .on("numDocumento", Sort.Direction.ASC)
                .named("unique_cuenta_documento")
                .unique()
                .partial(PartialIndexFilter.of(org.bson.Document.parse("{'numDocumento': {'$type': 'string'}}")))
        );
    }

    @RollbackExecution
    public void rollback() {
        template.indexOps("cuenta").dropIndex("unique_cuenta_documento");
    }
}
