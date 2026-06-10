package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.ItemPedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ItemPedido entity.
 */
@Repository
public interface ItemPedidoRepository extends MongoRepository<ItemPedido, String> {
    @Query("{}")
    Page<ItemPedido> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<ItemPedido> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<ItemPedido> findOneWithEagerRelationships(String id);
}
