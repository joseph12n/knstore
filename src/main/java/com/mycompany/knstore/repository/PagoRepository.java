package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Pago;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Pago entity.
 */
@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {
    Page<Pago> findByPedidoCuentaUserLogin(String login, Pageable pageable);

    Optional<Pago> findByIdAndPedidoCuentaUserLogin(String id, String login);
}
