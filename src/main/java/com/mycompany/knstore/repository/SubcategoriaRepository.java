package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Subcategoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Subcategoria entity.
 */
@Repository
public interface SubcategoriaRepository extends MongoRepository<Subcategoria, String> {
    @Query("{}")
    Page<Subcategoria> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Subcategoria> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Subcategoria> findOneWithEagerRelationships(String id);
}
