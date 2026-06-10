package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.EtiquetaProducto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the EtiquetaProducto entity.
 */
@Repository
public interface EtiquetaProductoRepository extends MongoRepository<EtiquetaProducto, String> {
    @Query("{}")
    Page<EtiquetaProducto> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<EtiquetaProducto> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<EtiquetaProducto> findOneWithEagerRelationships(String id);
}
