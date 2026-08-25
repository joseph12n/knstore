package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Pago;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Pago entity.
 */
@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {
    Page<Pago> findByPedidoId(String login, Pageable pageable);

    /** Consulta por lote: pagos de una coleccion de pedidos (RNF-028: elimina N+1). */
    @Query("{ 'pedido.$id': { $in: ?0 } }")
    Page<Pago> findByPedidoIdIn(Collection<ObjectId> pedidoIds, Pageable pageable);

    /** Consulta por lote sin paginar, para resolver pagos intermedios (facturas). */
    @Query("{ 'pedido.$id': { $in: ?0 } }")
    List<Pago> findByPedidoIdIn(Collection<ObjectId> pedidoIds);

    Optional<Pago> findByIdAndPedidoId(String id, String login);

    Optional<Pago> findByReferenciaPasarela(String referenciaPasarela);
}
