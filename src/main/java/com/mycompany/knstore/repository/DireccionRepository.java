package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Direccion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Direccion entity.
 */
@Repository
public interface DireccionRepository extends MongoRepository<Direccion, String> {
    Page<Direccion> findByCuentaUserLogin(String login, Pageable pageable);

    List<Direccion> findByCuentaUserLoginAndPedidoIsNull(String login);

    Optional<Direccion> findByIdAndCuentaUserLogin(String id, String login);
}
