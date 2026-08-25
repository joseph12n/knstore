package com.mycompany.knstore.config.dbmigrations;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.service.util.MoneyUtils;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.util.List;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Indices y backfill del catalogo de productos.
 * <ol>
 *   <li>RNF-027: indice de texto sobre nombre, descripcion y sku. La busqueda
 *   publica hoy usa $regex (texto parcial); el indice queda disponible para
 *   consultas de texto nativas y los $regex coexistiran con el.</li>
 *   <li>RF-072: backfill idempotente del campo denormalizado {@code precio_venta}
 *   sobre los productos que ya existen: se resuelve el {@code producto_precio}
 *   asociado y se persiste su precio de venta normalizado a 2 decimales.</li>
 * </ol>
 */
@ChangeUnit(id = "producto-indexes", order = "007")
public class ProductoIndexesMigration {

    /** Indice de texto compuesto equivalente a {@code IndexType.TEXT} para nombre, descripcion y sku. */
    private static final IndexDefinition INDICE_TEXT_PRODUCTO = new IndexDefinition() {
        @Override
        public Document getIndexKeys() {
            return new Document("nombre", "text").append("descripcion", "text").append("sku", "text");
        }

        @Override
        public Document getIndexOptions() {
            return new Document("name", "producto_search_text");
        }
    };

    private final MongoTemplate template;

    public ProductoIndexesMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        template.indexOps("producto").ensureIndex(INDICE_TEXT_PRODUCTO);

        backfillPrecioVenta();
    }

    /**
     * Backfill idempotente de RF-072: para cada producto sin {@code precio_venta}
     * (o nulo) se resuelve su {@code producto_precio} y se normaliza el valor a
     * 2 decimales con MoneyUtils (RNF-026). El catalogo de desarrollo es pequeno,
     * por lo que se itera con MongoTemplate sin limites.
     */
    private void backfillPrecioVenta() {
        List<Producto> productos = template.find(
            Query.query(new Criteria().orOperator(Criteria.where("precio_venta").exists(false), Criteria.where("precio_venta").is(null))),
            Producto.class
        );
        for (Producto producto : productos) {
            if (producto.getPrecio() == null || producto.getPrecio().getId() == null) {
                continue;
            }
            ProductoPrecio precio = template.findById(producto.getPrecio().getId(), ProductoPrecio.class);
            if (precio == null || precio.getPrecioVenta() == null) {
                continue;
            }
            template.updateFirst(
                Query.query(Criteria.where("_id").is(producto.getId())),
                new Update().set("precio_venta", MoneyUtils.normalizar(precio.getPrecioVenta())),
                Producto.class
            );
        }
    }

    @RollbackExecution
    public void rollback() {
        template.indexOps("producto").dropIndex("producto_search_text");
    }
}
