package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.ProductoPrecio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ProductoPrecio entity.
 */
@Repository
public interface ProductoPrecioRepository extends MongoRepository<ProductoPrecio, String> {}
