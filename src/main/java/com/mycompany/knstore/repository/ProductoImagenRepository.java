package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.ProductoImagen;
import java.util.Collection;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ProductoImagen entity.
 */
@Repository
public interface ProductoImagenRepository extends MongoRepository<ProductoImagen, String> {
    List<ProductoImagen> findByProductoId(String productoId);

    /**
     * RNF-028: los {@code @DBRef} se persisten con {@code $id} como {@link ObjectId};
     * la consulta por lote debe recibir {@code ObjectId} (ver {@code MongoIdUtils}).
     */
    @Query("{ 'producto.$id': { $in: ?0 } }")
    List<ProductoImagen> findByProductoIdIn(Collection<ObjectId> productoIds);
}
