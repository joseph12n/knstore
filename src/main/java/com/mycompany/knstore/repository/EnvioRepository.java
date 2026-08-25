package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import java.util.Collection;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Envio entity.
 */
@Repository
public interface EnvioRepository extends MongoRepository<Envio, String> {
    Page<Envio> findByPedidoId(String login, Pageable pageable);

    /** Consulta por lote: envios de una coleccion de pedidos (RNF-028: elimina N+1). */
    @Query("{ 'pedido.$id': { $in: ?0 } }")
    Page<Envio> findByPedidoIdIn(Collection<ObjectId> pedidoIds, Pageable pageable);

    Optional<Envio> findByIdAndPedidoId(String id, String login);

    Page<Envio> findByEstado(EstadoEnvio estado, Pageable pageable);
}
