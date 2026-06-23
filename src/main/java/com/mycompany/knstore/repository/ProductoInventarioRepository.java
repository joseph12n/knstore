package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.ProductoInventario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ProductoInventario entity.
 */
@Repository
public interface ProductoInventarioRepository extends MongoRepository<ProductoInventario, String> {}
