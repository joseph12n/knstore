package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Envio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Envio entity.
 */
@Repository
public interface EnvioRepository extends MongoRepository<Envio, String> {}
