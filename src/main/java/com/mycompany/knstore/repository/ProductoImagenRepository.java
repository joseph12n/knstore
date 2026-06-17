package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.ProductoImagen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ProductoImagen entity.
 */
@Repository
public interface ProductoImagenRepository extends MongoRepository<ProductoImagen, String> {}
