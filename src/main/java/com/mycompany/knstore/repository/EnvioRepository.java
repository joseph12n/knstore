package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Envio;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Envio entity.
 */
@Repository
public interface EnvioRepository extends MongoRepository<Envio, String> {
    Page<Envio> findByPedidoCuentaUserLogin(String login, Pageable pageable);

    Optional<Envio> findByIdAndPedidoCuentaUserLogin(String id, String login);
}
