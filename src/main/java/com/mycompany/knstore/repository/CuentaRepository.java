package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Cuenta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Cuenta entity.
 */
@Repository
public interface CuentaRepository extends MongoRepository<Cuenta, String> {
    @Query("{}")
    Page<Cuenta> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Cuenta> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Cuenta> findOneWithEagerRelationships(String id);

    @Query("{'user.login': ?0}")
    Page<Cuenta> findByUserLogin(String login, Pageable pageable);

    @Query("{'id': ?0, 'user.login': ?1}")
    Optional<Cuenta> findByIdAndUserLogin(String id, String login);
}
