package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.ProductoInventario;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ProductoInventario entity.
 */
@Repository
public interface ProductoInventarioRepository extends MongoRepository<ProductoInventario, String> {
    List<ProductoInventario> findByIdIn(Collection<String> ids);
}
