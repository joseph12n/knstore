package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.HistorialEstado;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the HistorialEstado entity.
 */
@Repository
public interface HistorialEstadoRepository extends MongoRepository<HistorialEstado, String> {
    List<HistorialEstado> findByEntidadAndIdEntidadOrderByFechaAsc(String entidad, String idEntidad);
}
