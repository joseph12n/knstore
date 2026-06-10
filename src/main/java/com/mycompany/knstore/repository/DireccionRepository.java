package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Direccion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Direccion entity.
 */
@Repository
public interface DireccionRepository extends MongoRepository<Direccion, String> {}
