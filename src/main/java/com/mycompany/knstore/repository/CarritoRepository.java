package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Carrito;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Carrito entity.
 */
@Repository
public interface CarritoRepository extends MongoRepository<Carrito, String> {}
