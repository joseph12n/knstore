package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Factura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Factura entity.
 */
@Repository
public interface FacturaRepository extends MongoRepository<Factura, String> {}
