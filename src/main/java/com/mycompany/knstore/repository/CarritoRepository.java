package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Carrito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Carrito entity.
 */
@Repository
public interface CarritoRepository extends MongoRepository<Carrito, String> {
    List<Carrito> findByCuentaId(String cuentaId);

    Optional<Carrito> findByIdAndCuentaId(String id, String cuentaId);
}
