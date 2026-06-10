package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.VarianteProducto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the VarianteProducto entity.
 */
@Repository
public interface VarianteProductoRepository extends MongoRepository<VarianteProducto, String> {
    @Query("{}")
    Page<VarianteProducto> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<VarianteProducto> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<VarianteProducto> findOneWithEagerRelationships(String id);
}
