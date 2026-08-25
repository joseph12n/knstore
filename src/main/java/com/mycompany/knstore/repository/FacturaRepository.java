package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Factura;
import java.util.Collection;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Factura entity.
 */
@Repository
public interface FacturaRepository extends MongoRepository<Factura, String> {
    Page<Factura> findByPagoId(String login, Pageable pageable);

    /** Consulta por lote: facturas de una coleccion de pagos (RNF-028: elimina N+1). */
    @Query("{ 'pago.$id': { $in: ?0 } }")
    Page<Factura> findByPagoIdIn(Collection<ObjectId> pagoIds, Pageable pageable);

    Optional<Factura> findByIdAndPagoId(String id, String login);
}
