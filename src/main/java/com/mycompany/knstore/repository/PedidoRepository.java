package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Pedido entity.
 */
@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {}
