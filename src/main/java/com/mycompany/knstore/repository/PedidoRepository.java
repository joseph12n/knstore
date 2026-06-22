package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Pedido entity.
 */
@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {
    Page<Pedido> findByCuentaId(String login, Pageable pageable);

    List<Pedido> findByCuentaUserLoginAndEnvioIsNull(String login);

    List<Pedido> findByCuentaIdAndEnvioIsNull(String cuentaId);

    Optional<Pedido> findByIdAndCuentaId(String id, String login);
}
