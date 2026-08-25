package com.mycompany.knstore.service.util;

import java.util.Collection;
import java.util.Objects;
import org.bson.types.ObjectId;

/**
 * Utilidades para resolver ids de referencias {@code @DBRef}.
 *
 * <p>
 * Mongo persiste los {@code @DBRef} como {@code {"$ref": ..., "$id": <ObjectId>}}, guardando el
 * {@code $id} como {@link ObjectId} aunque el id en Java sea {@code String}. Por eso las consultas
 * por lote contra la ruta {@code varref.$id} deben pasar {@code ObjectId} y no {@code String}
 * (RNF-028).
 * </p>
 */
public final class MongoIdUtils {

    private MongoIdUtils() {
        // util static, sin instancias
    }

    /** Convierte una coleccion de ids a {@link ObjectId}; los ids invalidos se ignoran (no matchean). */
    public static Collection<ObjectId> toObjectIds(Collection<String> ids) {
        return ids.stream().map(MongoIdUtils::toObjectId).filter(Objects::nonNull).toList();
    }

    /** Convierte un id a {@link ObjectId} si el string es un ObjectId valido; null en caso contrario. */
    public static ObjectId toObjectId(String id) {
        return id != null && ObjectId.isValid(id) ? new ObjectId(id) : null;
    }
}
