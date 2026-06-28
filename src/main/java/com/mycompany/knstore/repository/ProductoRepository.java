package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Producto entity.
 */
@Repository
public interface ProductoRepository extends MongoRepository<Producto, String> {
    @Query("{}")
    Page<Producto> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Producto> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Producto> findOneWithEagerRelationships(String id);
}
