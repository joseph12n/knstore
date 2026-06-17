package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.CategoriaIVA;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the CategoriaIVA entity.
 */
@Repository
public interface CategoriaIVARepository extends MongoRepository<CategoriaIVA, String> {}
