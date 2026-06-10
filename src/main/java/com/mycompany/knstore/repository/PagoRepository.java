package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Pago;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Pago entity.
 */
@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {}
