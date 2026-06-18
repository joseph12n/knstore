package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Factura;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Factura entity.
 */
@Repository
public interface FacturaRepository extends MongoRepository<Factura, String> {
    Page<Factura> findByPagoPedidoCuentaUserLogin(String login, Pageable pageable);

    Optional<Factura> findByIdAndPagoPedidoCuentaUserLogin(String id, String login);
}
