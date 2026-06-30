package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.ItemCarrito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ItemCarrito entity.
 */
@Repository
public interface ItemCarritoRepository extends MongoRepository<ItemCarrito, String> {
    @Query("{}")
    Page<ItemCarrito> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<ItemCarrito> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<ItemCarrito> findOneWithEagerRelationships(String id);

    List<ItemCarrito> findByCarritoId(String login);

    Optional<ItemCarrito> findByIdAndCarritoId(String id, String login);
}
